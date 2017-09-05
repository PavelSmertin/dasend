package com.dasend.state.tolch;


import com.dasend.state.tolch.model.Rules;
import com.dasend.state.tolch.model.Tolch;

public class TolchCalculator {
    private Rules mRules;

    public Tolch calculate(String message) {

        initRules(message);

//        double[][] coefficients = {
//
//                {-30.02, -63.19, -11.02, -26.55, -17.3, 0, -32.67, 0, 0, -6.77, -15.74, 0, -9.77, -22.49, -21.84, 0, -24.07, 0, -7.4, -7.4, 0, 0, -13.81, 0, 0, -16.25, -28.04, -26.14, 0, -10.66, 0, 0, 0, -21.37, 0, -11.4, 0, 0, 0, -1.93, -24.08, -16.02,
//                        -15.82, 0, -0.91, -6.95, -9.36, 0, 0, 0, 0, 0, -16.45, 0, 0, - 6.79 },
//                {-1.69,},
//                {33.36,}
//        };


        double bad = mRules.getRule1() * (-30.02) + mRules.getRule10() * (-6.77) + mRules.getRule11() * (-15.74) + mRules.getRule13() * (-9.77) + mRules.getRule14() * (-22.49) + mRules.getRule15() * (-21.84) + mRules.getRule17() * (-24.07) + mRules.getRule19() * (-7.4) + mRules.getRule2() * (-63.19) + mRules.getRule20() * (-7.4) + mRules.getRule23() * (-13.81) + mRules.getRule26() * (-16.25) + mRules.getRule27() * (-28.04) + mRules.getRule28() * (-26.14) + mRules.getRule3() * (-11.02) + mRules.getRule30() * (-10.66) + mRules.getRule34() * (-21.37) + mRules.getRule36() * (-11.4) + mRules.getRule4() * (-26.55) + mRules.getRule40() * (-1.93) + mRules.getRule41() * (-24.08) + mRules.getRule42() * (-16.02) + mRules.getRule43() * (-15.82) + mRules.getRule45() * (-0.91) + mRules.getRule46() * (-6.95) + mRules.getRule47() * (-9.36) + mRules.getRule5() * (-17.3) + mRules.getRule53() * (-16.45) + mRules.getRule7() * (-32.67) - 6.79;
        double neutral = mRules.getRule1() * (-1.69) + mRules.getRule10() * 2.68 + mRules.getRule11() * (-1.88) + mRules.getRule14() * (-2.21) + mRules.getRule15() * (-2.6) + mRules.getRule17() * 0.29 + mRules.getRule18() * (-0.02) + mRules.getRule19() * 0.59 + mRules.getRule2() * 0.24 + mRules.getRule20() * 0.59 + mRules.getRule22() * (-3.9) + mRules.getRule26() * 2.09 + mRules.getRule27() * 1.32 + mRules.getRule28() * (-2.43) + mRules.getRule3() * (-3.13) + mRules.getRule30() * (-2.84) + mRules.getRule32() * (-0.61) + mRules.getRule33() * (-2.35) + mRules.getRule36() * (-2.89) + mRules.getRule4() * (-2.5) + mRules.getRule40() * (-1.94) + mRules.getRule41() * 2.36 + mRules.getRule43() * 1.58 + mRules.getRule45() * (-10.52) + mRules.getRule46() * 1.76 + mRules.getRule47() * 1.16 + mRules.getRule5() * 2.72 + mRules.getRule52() * (-1.46) + mRules.getRule53() * 2.33 + mRules.getRule7() * (-81.91) + 1.66;
        double good = mRules.getRule1() * 33.36 + mRules.getRule11() * 28.51 + mRules.getRule13() * 9.93 + mRules.getRule14() * 35.53 + mRules.getRule15() * 19.94 + mRules.getRule17() * 21.97 + mRules.getRule18() * 0.2 + mRules.getRule19() * 13.71 + mRules.getRule2() * 46.51 + mRules.getRule20() * 13.71 + mRules.getRule22() * 11.16 + mRules.getRule23() * 6.95 + mRules.getRule26() * 29.46 + mRules.getRule27() * 26.89 + mRules.getRule28() * 33.41 + mRules.getRule3() * 34.74 + mRules.getRule30() * 25.63 + mRules.getRule32() * 4.55 + mRules.getRule33() * 14.36 + mRules.getRule34() * 17.18 + mRules.getRule36() * 36.41 + mRules.getRule4() * 46.08 + mRules.getRule40() * 17.68 + mRules.getRule41() * 29.07 + mRules.getRule42() * 11.21 + mRules.getRule43() * 4.61 + mRules.getRule45() * 15.56 + mRules.getRule47() * 2.56 + mRules.getRule5() * 2.68 + mRules.getRule52() * 8.18 + mRules.getRule53() * 40.48 + mRules.getRule55() * 2.83 + mRules.getRule7() * 54.61 - 10.99;

        return new Tolch(good, bad, neutral);
    }




    private void initRules(String message) {
        mRules = new Rules();

        if(message == null) {
            return;
        }

        mRules = new Rules();

        // убираем лишние символы
        message = message.replace(":", "");
        message = message.replace("-", "");

        // разделяем слова в смс
        String[] words = message.split("\\s+");

        // начинаем работу по 61 правилам Толмачева
        // идем с конца строки к началу прорабатывая каждое правило
        // с конца на начало нужно двигаться, т.к.существуют предшествующие объекты, которые отменяют либо
        // оборачивают правило
        words = reverse(words);

        boolean fistWord = true;
        String prevWord = "";
        for (String word : words) {
            // удаляем пробелы в словах
            word = word.replace(" ", "");
            word = word.toLowerCase();

            int wordLenght = word.length();

            //1 - смайлики
            int rule1 = substringCount(word, ")") + substringCount(word, "-P") + substringCount(word, "*") + substringCount(word, "^") - substringCount(word, "(");
            mRules.incRule1(rule1);

            //2 - знаки
            int rule2 = 0;
            int condition_2_1 = substringCount(word, "?!") + substringCount(word, "!?");
            if (condition_2_1 == 0) {
                int condition_2_2 = substringCount(word, "!");
                int condition_2_3 = substringCount(word, "?");
                if (condition_2_2 > 1) {
                    rule2 = rule2 + condition_2_2;
                }
                if (condition_2_3 > 1) {
                    rule2 = rule2 - condition_2_3;
                }
            }

            mRules.incRule2(rule2);

            //3 - уменьшительно ласкательные суффиксы
            int rule3 = 0;
            String[] dict3 = {"чка", "уля", "йка", "чка", "тик", "шка", "шко", "нький",
                    "нько", "кни", "юша", "ська", "дки", "ньку", "тка", "сики",
                    "нька", "хин", "юхе", "шка", "чик", "нки", "нка", "ться",
                    "юха", "юша", "уля", "лькин", "жку", "чик", "мка"};

            if (wordLenght > 3) {
                String suffix = word.substring(wordLenght-3, wordLenght);
                if (existValues(dict3, new String[]{suffix})) {
                    rule3 = 1;
                }
            } else if (word.length() > 4) {
                String suffix = word.substring(wordLenght-4, wordLenght);
                if (existValues(dict3, new String[]{suffix})){
                    rule3 = 1;
                }
            } else if (word.length() > 5) {
                String suffix = word.substring(wordLenght-5, wordLenght);
                if (existValues(dict3, new String[]{suffix})){
                    rule3 = 1;
                }
            }

            mRules.incRule3(rule3);

            //4 - явные слова
            int rule4 = 0;
            String[] dict4_1 = {"хорошо", "хороший", "хорошие", "отлично", "плохо", "нормально",
                    "любимая", "понравится", "люблю", "рада", "любимый", "любимая",
                    "лучше", "поцелую", "поцелуй", "целую", "обнимаю", "желаю", "молодец",
                    "целую", "лучший", "любящий", "надежный", "счастье", "счастлив"};
            String[] dict4_2 = {"испортил", "плохо", "отстой"};

            if (existValues(dict4_1, word)){
                rule4 = 1;
            }
            if (existValues(dict4_2, word)){
                rule4 = -1;
            }
            mRules.incRule4(rule4);

            //5 - матерные слова
            int rule5 = 0;
            String[] dict5 = {"говно", "сука", "хуево", "хуй", "мутишь", "хуле", "охуел", "ебал",
                    "блять", "бля", "ебу", "нах", "пизда", "пиздеть", "пиздец", "пизданутый",
                    "пизданутая", "пизданутые", "ахуе", "ахренеть", "капец", "ебанутый", "ебан",
                    "заебал", "нахуй", "нах", "сука", "дохуя", "пизди", "нефига", "минет", "ебошишь",
                    "еб", "ебу", "мразь", "мрази", "мразота"};

            if (existValues(dict5, word)){
                rule5 = -1;
            }
            mRules.incRule5(rule5);

            //6 - увеличение и уменьшение
            int rule6 = 0;
            String[] dict6_1 = {"пришло", "прибыль", "увеличилось", "увеличение"};
            String[] dict6_2 = {"ушло", "уменьшилось", "упало"};
            if (existValues(dict6_1, word)){
                rule6 = 1;
            }
            if (existValues(dict6_2, word)){
                rule6 = -1;
            }
            mRules.incRule6(rule6);

            //7 - стандарты автоответов
            int rule7 = 0;
            String[] dict7 = {"перезвонить", "абонент", "услуга", "пропущенный", "просьба", "получена", "доставлено", "перезвоните", "звонил", "пропущен", "едет"};
            if (existValues(dict7, word)){
                rule7 = 1;
            }
            mRules.incRule7(rule7);

            //8 - жизнь хорошо, смерть плохо
            int rule8 = 0;
            String[] dict8 = {"убью", "пожалеешь"};
            if (existValues(dict8, word)){
                rule8 = -1;
            }
            mRules.incRule8(rule8);

            //9 - друг хорошо, враг плохо
            int rule9 = 0;
            String[] dict9_1 = {"друг", "дружба", "друзья", "дружить"};
            String[] dict9_2 = {"враг", "вражда", "война", "воюем", "воевать", "вражина"};
            if (existValues(dict9_1, word)){
                rule9 = 1;
            }
            if (existValues(dict9_2, word)){
                rule9 = -1;
            }
            mRules.incRule9(rule9);

            //10 - ужас, страх это плохо
            int rule10 = 0;
            String[] dict10 = {"страшно", "страх", "боюсь", "переживаю", "волнуюсь", "ужас", "жестоко"};
            if (existValues(dict10, word)){
                rule10 = -1;
            }
            mRules.incRule10(rule10);

            //11 - троеточие или две точки чаще плохо, больше 3 точек чаще хорошо
            int rule11 = 0;
            if (substringCount(word, ".") <= 3 && substringCount(word, ".") > 1) {
                rule11 = -1;
            } else if (substringCount(word, ".") > 3) {
                rule11 = 1;
            }
            mRules.incRule11(rule11);

            //12 - стандарты магазинов это позитивчик
            int rule12 = 0;
            String[] dict12 = {"распродажа", "акция"};
            if (existValues(dict12, word)){
                rule12 = 1;
            }
            mRules.incRule12(rule12);

            //13 - когда скучают это хорошо
            int rule13 = 0;
            String[] dict13 = {
                    "скучаем", "соскучилась", "ждем", "жду", "дождусь", "ожидание", "встречу"};
            if (existValues(dict13, word)){
                rule13 = 1;
            }
            mRules.incRule13(rule13);

            //14 - приветствие это хорошо
            int rule14 = 0;
            String[] dict14 = {"привет", "здравствуй", "здарова", "добрый", "здорова",
                    "здравие", "здравствуйте", "добрым", "утром", "доброго",
                    "добро", "сладкого", "сладкий", "сладких", "доброе"};
            if (existValues(dict14, word)){
                rule14 = 1;
            }
            mRules.incRule14(rule14);

            //15 - тянущиеся гласные это хорошо
            int rule15 = 0;
            String wooord = "";


            char[] letters = {'а', 'о', 'и', 'е', 'ё', 'э', 'ы', 'у', 'ю', 'я'};
            char prevSymbol = ' ';
            for (int i = 0; i < word.length(); i++) {
                if (prevSymbol == word.charAt(i) && existValues(letters, word.charAt(i))) {
                    wooord += "1";
                } else {
                    wooord += "0";
                }
                prevSymbol = word.charAt(i);
            }

            String[] symbols = wooord.split("0+");

            for (String symbol : symbols ) {
                if (symbol.length() > 2) {
                    rule15 = 1;
                }
            }
            mRules.incRule15(rule15);

            //16 - эмоции
            int rule16 = 0;
            String[] dict16_2 = {"плохо", "истерика", "истеричка", "разочарование", "разочаровал", "разочарую", "разочарован"};
            if (existValues(dict16_2, word)){
                rule16 = -1;
            }
            mRules.incRule16(rule16);

            //17 - срочность это плохо, отрицание срочности хорошо
            int rule17 = 0;
            String[] dict17_2 = {"срочно"};
            if (existValues(dict17_2, word)){
                rule17 = -1;
            }
            mRules.incRule17(rule17);

            //18 - количество слов в смс
            int rule18 = 1;
            mRules.incRule18(rule18);

            //19 - предлог "бы" чаще плохо
            int rule19 = 0;
            if (word.equals("бы")) {
                rule19 = -1;
            }
            mRules.incRule19(rule19);

            //20 - предлог "не" чаще плохо
            int rule20 = 0;
            if (word.equals("не")) {
                rule20 = -1;
            }
            mRules.incRule20(rule20);

            //21 - отсутствие "никто" чаще плохо
            int rule21 = 0;
            if (word.equals("никто")) {
                rule21 = -1;
            }
            mRules.incRule21(rule21);

            //22 - интерес это хорошо
            int rule22 = 0;
            String[] dict22 = {"интерес", "интересно", "вау", "ого", "расскажи", "очень"};
            if (existValues(dict22, word)){
                rule22 = 1;
            }
            mRules.incRule22(rule22);

            //23 - нужен это хорошо
            int rule23 = 0;
            String[] dict23 = {"нужен", "нужна", "нужны"};
            if (existValues(dict23, word)){
                rule23 = 1;
            }
            mRules.incRule23(rule23);

            //24 - действие это хорошо
            int rule24 = 0;
            String[] dict24 = {"гоу", "го", "пошли", "поход", "путешествие", "путешествовать", "пойдем"};
            if (existValues(dict24, word)){
                rule24 = 1;
            }
            mRules.incRule24(rule24);

            //25 - отсутствие действия это плохо
            int rule25 = 0;
            String[] dict25 = {"стой", "остановись", "сядь"};
            if (existValues(dict24, word)){
                rule25 = -1;
            }
            mRules.incRule25(rule25);

            //26 - суффикс повелительный это плохо
            int rule26 = 0;
            String[] dict26_0 = {"рди", "тят", "нишь"};
            String[] dict26 = {
                    "скинь", "сделай", "ставь", "посмотри", "подтверди", "ответят", "звони", "старайся", "возьми"
                    , "купи", "отнеси", "забери", "скажешь", "передашь", "сделаешь", "следи", "ответь", "назовешь"
                    , "ответь", "позвони", "переведешь", "скинь", "ищи", "явиться", "перезвони", "едь"
                    , "спроси", "пометь", "напиши", "пиши", "вырывай", "скажи", "вызови", "выйди", "ответь", "отвечай"
                    , "позвони", "забудь", "наберешь", "будь", "сходи", "разберись", "отвези", "пополни", "принеси", "подай"
                    , "позвони", "спеши", "переведи", "попроси"};
            if (existValues(dict26, word)) {
                rule26 = -1;
            }
            else if (word.length() > 3) {
                if (existValues(dict26_0, word.substring(wordLenght-4, wordLenght -1)) ) { // последние 3 символа слова
                    rule26 = -1;
                }
            }
            mRules.incRule26(rule26);

            //27 - быстрое действие это хорошо перед нейтральным словом это плохо
            int rule27 = 0;
            String[] dict27 = {"быстро", "сразу", "мигом", "срочно"};
            if (existValues(dict27, word)) {
                rule27 = -1;
            }
            mRules.incRule27(rule27);

            //28 - согласие это хорошо
            int rule28 = 0;
            String[] dict28 = {"ладно", "хорошо", "договорились", "успешно", "приму", "да", "ок", "понятно", "круто", "давай", "окей"};
            if (existValues(dict28, word)){
                rule28 = 1;
            }
            mRules.incRule28(rule28);

            //29 - наказание это плохо
            int rule29 = 0;
            String[] dict29 = {"издеваешься", "наказание", "наказан"};
            if (existValues(dict29, word)){
                rule29 = -1;
            }
            mRules.incRule29(rule29);

            //30 - благодарность это хорошо
            int rule30 = 0;
            String[] dict30 = {"спасибо", "благодарю", "молодец", "спс"};
            if (existValues(dict30, word)){
                rule30 = 1;
            }
            mRules.incRule30(rule30);

            //31 - жаргон это плохо
            int rule31 = 0;
            String[] dict31 = {"нал", "бабки", "тварь", "фига", "трубу", "охренел"};
            if (existValues(dict31, word)){
                rule31 = -1;
            }
            mRules.incRule31(rule31);

            //32 - отдых это хорошо
            int rule32 = 0;
            String[] dict32 = {"отдых", "отдыхаем", "расслаблены", "расслаблен", "кайф", "курорт", "отпуск"
                    , "выходные", "путешествие", "туризм"};
            if (existValues(dict32, word)){
                rule32 = 1;
            }
            mRules.incRule32(rule32);

            //33 - пожелания это хорошо
            int rule33 = 0;
            String[] dict33 = {"спокойной", "доброй", "хорошей", "замечательной", "сладких", "целую"
                    , "поправляйся", "выздаравливай", "крепись", "держись", "постарайся"};
            if (existValues(dict33, word)){
                rule33 = 1;
            }
            mRules.incRule33(rule33);

            //34 - мольба это хорошо
            int rule34 = 0;
            String[] dict34 = {
                    "пожалуйста", "помоги", "спаси", "прости", "извини", "помочь", "вина", "прошу", "плиз"};
            if (existValues(dict34, word)){
                rule34 = 1;
            }
            mRules.incRule34(rule34);

            //35 - радостные слова
            int rule35 = 0;
            String[] dict35 = {"приколюх", "ржем", "смешно"};
            if (existValues(dict35, word)){
                rule35 = 1;
            }
            mRules.incRule35(rule35);

            //36 - Уменьшительные имена это хорошо
            int rule36 = 0;
            String[] dict36 = {
                    "димон", "саня", "аленка", "натуль", "дашка", "братан", "ань", "наташ", "ната"
                    , "миха", "жека", "женька", "катин", "сашь", "катька", "сашка", "сашуля", "сашуль"
                    , "мих", "денчик", "леха", "лех", "алеш", "дашка", "дашкой", "илюха", "илюх", "саня"
                    , "санек", "гришуля", "кирюша", "илюха", "дань", "саш", "марин", "даня", "данилка"
                    , "дань", "толик", "толян", "жень", "женька", "ден", "наташ", "наташа", "бро", "макс"
                    , "сынок", "сереж", "сережа", "женек", "димка", "димон", "братан", "братиш", "братишка", "серега"};
            if (existValues(dict36, word)){
                rule36 = 1;
            }
            mRules.incRule36(rule36);

            //37 - скука это хорошо
            int rule37 = 0;
            String[] dict37 = {"скучаю", "жалею", "волнуюсь", "желаю"};
            if (existValues(dict37, word)){
                rule37 = 1;
            }
            mRules.incRule37(rule37);

            //38 - волнение это плохо
            int rule38 = 0;
            String[] dict38 = {"переживай", "жалей", "волнуйся", "страдай", "болей"};
            if (existValues(dict38, word)){
                rule38 = -1;
            }
            mRules.incRule38(rule38);

            //39 - предлог некогда плохой
            int rule39 = 0;
            if (word.equals("некогда")) {
                rule39 = -1;
            }
            mRules.incRule39(rule39);

            //40 - наименование животным это хорошо
            int rule40 = 0;
            String[] dict40 = {"зай", "кролик", "котик", "тигр", "зайка", "зайчонок", "пушистик"
                    , "рыбка", "зайчик", "киса", "заяц", "котенок"};
            if (existValues(dict40, word)){
                rule40 = 1;
            }
            mRules.incRule40(rule40);

            //41 - отказ это плохо
            int rule41 = 0;
            String[] dict41 = {"нет", "отрицаю", "неа"};
            if (existValues(dict41, word)){
                rule41 = -1;
            }
            mRules.incRule41(rule41);

            //42 - сочетание знаков ! ? или ? !это плохо
            int rule42 = 0;
            rule42 = -1 * (substringCount(word, "?!") + substringCount(word, "!?"));
            mRules.incRule42(rule42);

            //43 - обязательства это плохо
            int rule43 = 0;
            String[] dict43 = {"нужно", "обязан", "должен", "должна", "долг"};
            if (existValues(dict43, word)){
                rule43 = -1;
            }
            mRules.incRule43(rule43);


            //44 - злось это плохо
            int rule44 = 0;
            String[] dict44 = {
                    "злись", "жестоко", "плохо", "скуп", "тягость", "бред", "бесят", "раздражают", "стресс"
                    , "пофиг", "переживать"};
            if (existValues(dict44, word)){
                rule44 = -1;
            }
            mRules.incRule44(rule44);

            //45 - добрые пожелания это хорошо
            int rule45 = 0;
            String[] dict45 = {
                    "удачи", "счастья", "поздравляю", "здоровья", "любви", "страсти", "здоровья", "сердечно"
                    , "желаю", "дорог", "подравляем", "поздравление"};
            if (existValues(dict45, word)){
                rule45 = 1;
            }
            mRules.incRule45(rule45);

            //46 - оскорбления это плохо
            int rule46 = 0;
            String[] dict46 = {"тупой", "дурак", "идиот", "сука", "проститутка", "позор"};
            if (existValues(dict46, word)){
                rule46 = -1;
            }
            mRules.incRule46(rule46);

            //47 - начало предложения с "И" или "АУ" это плохо
            int rule47 = 0;
            String[] dict47 = {"и", "ау"};

            if (fistWord && existValues(dict47, word)){
                rule47 = -1;
            }

            mRules.incRule47(rule47);

            //48 - траты это плохо
            int rule48 = 0;
            String[] dict48 = {"траты", "трать", "растраты", "потеря", "утрата", "убыток", "минус"};
            if (existValues(dict48, word)){
                rule48 = -1;
            }
            mRules.incRule48(rule48);

            //49 - обман это плохо
            int rule49 = 0;
            String[] dict49 = {
                    "жулик", "обман", "шулер", "мошенник", "плевать", "обманул", "обманщик"
                    , "обманщица", "наплевал", "заблевал"};
            if (existValues(dict49, word)){
                rule49 = -1;
            }
            mRules.incRule49(rule49);

            //50 - свобода это хорошо
            int rule50 = 0;
            String[] dict50 = {"свободна", "освободилась"};
            if (existValues(dict50, word)){
                rule50 = 1;
            }
            mRules.incRule50(rule50);

            //51 - упоминание стран курорта это хорошо
            int rule51 = 0;
            String[] dict51 = {"турция", "корсика", "монако", "милан", "тунис", "вьетам", "гоа", "кипр", "египет", "китай", "япония"};
            if (existValues(dict51, word)){
                rule51 = 1;
            }
            mRules.incRule51(rule51);

            //52 - комбинация ахахаха это хорошо
            int rule52 = 0;
            if (substringCount(word, "ха") >= 2) {
                rule52 = 1;
            }
            mRules.incRule52(rule52);

            //53 - не это плохо
            int rule53 = 0;
            if (word.equals("не")) {
                rule53 = -1;
            }
            mRules.incRule53(rule53);

            //53.1 - ничего перед не убирает отрицание не
            if (word.equals("ничего") && prevWord.equals("не")) {
                mRules.incRule53(1);
            }

            //53.2 - или перед не и нет убирает плохое состояние
            if (word.equals("или") && prevWord.equals("не")) {
                mRules.incRule53(1);
            }

            //54 - радость это хорошо
            int rule54 = 0;
            String[] dict54 = {"супер", "класс", "прекрасно", "клас", "великолепно", "радость", "вау", "волшебно", "чудесно", "оболденно", "обалденно", "ачуметь", "ура", "бомба", "праздник", "праздником"};
            if (existValues(dict54, word)){
                rule54 = 1;
            }
            mRules.incRule54(rule54);

            //55 - когда что - либо нравится это хорошо
            int rule55 = 0;
            String[] dict55 = {"понравилось", "нравится", "понравится", "нравилось", "классно", "класно", "понравились", "нравились"};
            if (existValues(dict55, new String[]{word})){
                rule55 = 1;
            }
            mRules.incRule55(rule55);


            //запоминаем информацию по текущему слову
            prevWord = word;

            fistWord = false;

        }
    }

    private String[] reverse(String[] words) {
        String[] result = new String[words.length];
        for (int i = 0; i < words.length; i++) {
            result[words.length - i - 1]  = words[i];
        }
        return result;

    }

    private int substringCount (String str, String substr){

        int lastIndex = 0;
        int count = 0;

        while(lastIndex != -1){

            lastIndex = str.indexOf(substr,lastIndex);

            if(lastIndex != -1){
                count ++;
                lastIndex += substr.length();
            }
        }

        return count;
    }

    private boolean existValues(char[] dict, char symbol) {
        for(char symbolDict : dict) {
            if(symbol == symbolDict) {
                return true;
            }
        }
        return false;
    }

    private boolean existValues(String[] dict, String value) {
        for(String word : dict) {
            if(value.equals(word)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean existValues(String[] dict, String[] values) {
        for(String value : values) {
            for(String word : dict) {
                if(value.equals(word)) {
                    return true;
                }
            }
        }
        return false;
    }

}
