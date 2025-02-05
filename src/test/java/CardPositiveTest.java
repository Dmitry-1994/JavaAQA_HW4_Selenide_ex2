import com.codeborne.selenide.Condition;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class CardPositiveTest {

    private String getData(int countDay, String format) {
        return LocalDate.now().plusDays(countDay).format(DateTimeFormatter.ofPattern(format));
    }

    private int getMonth(int countDay) {
        return LocalDate.now().plusDays(countDay).getMonthValue();
    }

    private int getYear(int countDay) {
        return LocalDate.now().plusDays(countDay).getYear();
    }

    private void setMonth(int differenceOfMonth) {
        while (differenceOfMonth > 0) {
            $(".calendar__arrow[data-step='1']").click();
            differenceOfMonth--;
            }
        while (differenceOfMonth < 0) {
            $(".calendar__arrow[data-step='-1']").click();
            differenceOfMonth++;
            }
    }

    private void setYear(int differenceOfYear) {
        while (differenceOfYear > 0) {
            $(".calendar__arrow[data-step='12']").click();
            differenceOfYear--;
        }
    }

    @ParameterizedTest
    @CsvSource({
            "Ек, Екатеринбург, 7, Дмитрий, +79122518775",
            "мо, Москва, 28, Дмитрий Тарасов, +12345678910",
            "на, Ростов-на-Дону, 365, Дмитрий-Тарасов Алексеевич, +99999999999",
            "на, Ростов-на-Дону, 415, Дмитрий-Тарасов Алексеевич, +99999999999",
            "на, Ростов-на-Дону, 750, Дмитрий-Тарасов Алексеевич, +99999999999"
    })
    void shouldRegisterAccountWithUniversalDate(String cityShort, String cityFull, int correctData, String name, String phone) {
        open("http://localhost:9999/");
        $("[data-test-id=city] input").setValue(cityShort);
        $$(".menu-item__control").findBy(Condition.exactText(cityFull)).shouldBe(visible).click();

        $("[data-test-id=date] .icon").click();
        String setData = getData(correctData, "d");
        int differenceOfMonth = getMonth(correctData) - getMonth(0);
        int differenceOfYear = getYear(correctData) - getYear(0);
        setYear(differenceOfYear);
        setMonth(differenceOfMonth);
        $$(".calendar__day").findBy(Condition.exactText(setData)).shouldBe(visible).click();

        $("[data-test-id=name] input").setValue(name);
        $("[data-test-id=phone] input").setValue(phone);
        $("[data-test-id='agreement']").click();
        $(".button__text").click();
        $("[data-test-id=notification]").shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=notification] .notification__title").shouldHave(text("Успешно!"));
        String expectedData = getData(correctData, "dd.MM.yyyy");
        $("[data-test-id=notification] .notification__content").shouldHave(text("Встреча успешно забронирована на " + expectedData));

    }
}
