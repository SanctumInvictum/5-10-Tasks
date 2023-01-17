package com.BVT2105;

import java.util.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Основной класс, содержащий таски
public class Main{
    // Основная функция, для ввода и вывода тестов тасков
    public static void main(String[] args) {
        // вывод тасков
        System.out.println("№1.1\t" + encrypt("Hello"));
        System.out.println("№1.2\t" + decrypt(strArrToIntArr(new String[]{ "72", "33", "-73", "84", "-12", "-3", "13", "-13", "-68"})));
        System.out.println("№2\t" + canMove("Rook","A8","H8"));
        System.out.println("№3\t" + canComplete("butl", "beautiful"));
        System.out.println("№4\t" + sumDigProd(new int[]{1,2,3,4,5,6}));
        System.out.println("№5\t" + sameVowelGroup(new String[]{"toe","ocelot","maniac"}));
        System.out.println("№6\t" + validateCard("1234567890123456"));
        System.out.println("№7\t" + numToEng(126));
        System.out.println("№8\t" + hash("password123", ""));
        System.out.println("№9\t" + correctTitle("jOn SnoW, kINh IN thE noRth."));
        System.out.println("№10\t" + hexLattice(19));
    }

    // №1: Кодирование и декодирование
    //"Encrypts" - функция, принимающая строку и кодирующая ее в массив чисел, где первое число - код первого символа,
    // а последующие - разница между символьным кодом предыдущего и текущего символа
    public static int[] encrypt(String s) {
        // Зададим переменную предыдущего символа, которая впоследствии сменится в цикле
        char prev = ';';
        // Создадим маркер первого прохода цикла, чтоб запомнить значение прошлого символа prev
        boolean first = true;
        // создаем результирующий массив размером - кол-во символов в строке
        int[] result = new int[s.length()];
        // Запустим цикл, который при первой итерации занесет в результирующий массив индекс первого символа,
        // затем поменяет маркер, запомнит этот символ как предыдущий и затем будет заносить разность в массив
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(!first) {
                result[i] = c - prev;
            } else {
                result[i] = c;
                first = false;
            }
            prev = c;
        }
        return result;
    }

    //"Decrypts" - функция, принимающая массив и декодирующая его в строку
    public static String decrypt(int[] arr) {
        // Создадим переменную отвечающую за число, в последствие декодируемое в символ
        char curr = 0;
        // Создадим результирующую строку
        String result = "";
        // Запустим цикл по длине массивы, который будет "собирать" итоговую строку преобразуя, код в символ
        // и добавлять его к строке, затем увеличивать переменную кода
        for(int i = 0; i < arr.length; i++) {
            curr += arr[i];
            result = result.concat(Character.toString(curr));
        }
        return result;
    }

    // №2: функция, которая принимает имя шахматной фигуры, ее положение и целевую позицию и возвращает возможность
    // движения фигуры к цели
    public static boolean canMove(String figure, String startp, String endp) {
        // Преобразуем стартовую и конечную позицию в числа, первое и третье - номер буквенной позиции, где A = 0, B = 1
        // и т.д. , а второе и последнее номер численной позиции, где '1' = 0, '2' = 1 и т.д.
        int startx = startp.charAt(0) - 'A';
        int starty = startp.charAt(1) - '1';
        int endx = endp.charAt(0) - 'A';
        int endy = endp.charAt(1) - '1';
        // Напишем условия для каждой фигуры:
        // Пешка может ходить только по прямой, поэтому буквенные координаты позиций должны быть равны, пешка не может
        // ходить дальше, чем на 3 клетки, поэтому расстояние должно быть меньше 3, пешка не может ходить назад, поэтому начальная координата меньше конечной
        if(figure.equals("BottomPawn")) return (startx == endx && endy-starty < 3 && starty < endy);
        else if(figure.equals("TopPawn")) return (startx == endx && endy-starty > -3 && starty > endy);
        // ладья может ходить пока равны буквенные или символьные координаты
        else if(figure.equals("Rook")) return (startx == endx || starty == endy);
        // Конь может ходить когда буквенная координата отличается на две, а численная на одну и наоборот
        else if(figure.equals("Knight")) return ((Math.abs(startx-endx) == 2 && Math.abs(starty-endy) == 1) || (Math.abs(startx-endx) == 1 && Math.abs(starty-endy) == 2));
        // слон может ходить по диагонали, а значит изменение буквенной координаты должно быть равно изменению численной
        else if(figure.equals("Bishop")) return (Math.abs(startx-endx) == Math.abs(starty-endy));
        // Ферзь может ходить как ладья, а как слон, значит любой из вариантов хода этих фигур
        else if(figure.equals("Queen")) return (Math.abs(startx-endx) == Math.abs(starty-endy) || startx == endx || starty == endy);
        //Король может перемещать на одну клетку в любую сторону, поэтому изменение буквы и цифры должно быть меньше двух
        else if(figure.equals("King")) return(Math.abs(startx-endx) < 2 && Math.abs(starty-endy) < 2);
        // Если хода не было, или нет такой фигуры, то ход невозможен
        return false;
    }

    // №3: функция, определяет возможность завершить слово, учитывая входную строку
    public static boolean canComplete(String s, String target) {
        int j = 0;
        // Проходимся циклом по слову-цели
        for(int i = 0; i < target.length(); i++) {
            // Запоминаем символ слова-цели
            char c = target.charAt(i);
            // проверяем является ли этот символ первым в незавершенном слове, если первый символ нашелся, проверяем
            // следующий, и так пока не пройдемся по всему слову-цели, если мы найдем в слове-цели все буквы, которые
            // содержит незавершенное, то слово можно завершить, иначе нельзя
            if(c == s.charAt(j)) {
                j++;
                if(j == s.length()) return true;
            }
        }
        return false;
    }

    // №4: Функция, принимающая числа, складывающая и возвращающая произведение цифр пока не получит ответ длинной в 1 знак
    public static int sumDigProd(int[] arr) {
        // введем переменную суммы чисел
        int n = 0;
        // сложим числа
        for (int i = 0; i < arr.length; i++) n+=arr[i];
        // пока ответ длиной больше 1 символа
        while(n/10 > 0) {
            // создадим перемножаемую переменную
            int temp = 1;
            // преобразуем сумму в строку
            String strtemp = Integer.toString(n);
            // пройдемся по каждой цифре в сумме, когда они все перемножатся до 1 цифры
            for(int i = 0; i < strtemp.length(); i++) {
                char c = strtemp.charAt(i);
                temp *= Integer.parseInt(Character.toString(c));
            }
            // внутри цикла передадим n нужное значение, до его завершения
            n = temp;
        }
        return n;
    }

    // №5: функция, выбирающая все слова, имеющие все те же гласные, что и первое слово, включая его
    public static List<String> sameVowelGroup(String[] args) {
        // Зададим список размером - кол-во вводимых слов
        ArrayList<List<Character> > vowelLists = new ArrayList<List<Character> >(args.length);
        List<String> result = new ArrayList<>();
        // создадим маркер на первую итерацию цикла
        boolean first = true;
        // запустим цикл по словам
        for(int i = 0; i < args.length; i++) {
            // Добавим слово в список
            vowelLists.add(getVowelList(args[i]));
            // на первой итерации, в результирующий список добавим первое слово, сменим маркер,
            // на последующих все слова сравнятся с первым и если гласные совпадут, то слово добавится в результат
            if(!first) {
                if(vowelLists.get(0).equals(vowelLists.get(i))) {
                    result.add(args[i]);
                }
            } else {
                result.add(args[i]);
                first = false;
            }
        }
        return result;
    }

    // под-функция для №5 реализующая сравнение гласных
    private static List<Character> getVowelList(String s) {
        List<Character> alist = new ArrayList<>();
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
                if(!alist.contains(c)) alist.add(c);
            }
        }
        Collections.sort(alist);
        return alist;
    }

    // №6: функция, которая принимает число и возвращает: является ли это число действительным номером кредитной карты
    // длина номера: 14-19 цифр, после удаления последней цифры, переворота числа, удовения значения каждой цифры в
    // нечетных позициях(если удвоенное >1 цифры, то сложить цифры), добавления всех цифр и вычитания последней цифры
    // суммы из 10, результат должен быть равен контрольной цифре из шага 1
    public static boolean validateCard(String s) {
        // проверяем длину номера
        if(s.length() < 14 || s.length() > 19) return false;
        int sum = 0;
        // доходим в цикле до предпоследнего символа
        for(int i = 0; i <= s.length()-2; i++) {
            // Создаем переменную в которую парсим перевернутое число, с удвоением значения каждой цифры в нечетных позициях
            int temp = Integer.parseInt(Character.toString(s.charAt(s.length()-i-2)))*((1-i%2)+1);
            // пока не получим 1 цифру, добавляем все цифры
            if (temp/10 > 0) {
                sum += temp/10;
                sum += temp%10;
            } else {
                sum += temp;
            }
        }
        // Выводим логический результат того, равна ли сумма контрольной цифре
        return Integer.toString(10-sum%10).charAt(0) == (s.charAt(s.length()-1));
    }

    // №7: функция, которая принимает положительное целое число от 0 до 999 и возвращает строковое представление числа на английском
    public static String numToEng(int n) {
        // Создадим результирующую строку для вывода
        String result = "";
        // Создадим массив, содержащий все возможные окончания последнего разряда
        String[] sub10s = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
        // Массив, содержащий все основные части чисел
        String[] roots = {"twen", "thir", "for", "fif", "six", "seven", "eigh", "nine"};
        // исключения встречающиеся по одному разу
        String[] exceptions = {"ten", "eleven", "twelve"};
        // Зададим три разряда, сотни, десятки и единицы
        int hundreds = n/100;
        int decades = n/10 - (n/100)*10;
        int other = n%10;
        // Если число с тремя разрядами, то в результат записываем значение последнего разряда с приставкой сотни
        if(hundreds > 0) {
            result = result.concat(sub10s[hundreds]+" hundred ");
        }
        // Если есть несколько десятков, то добавляем к результату их и оставшийся последний разряд
        if(decades > 1) {
            result = result.concat(roots[decades-2]+"ty ");
            result = result.concat(sub10s[other]);
            // если десяток один, то добавляем окончание teen
        } else if(decades == 1) {
            if(other > 2) result = result.concat(roots[other-2]+"teen");
            else result = result.concat(exceptions[other]);
        } else {
            result = result.concat(sub10s[other]);
        }
        return result;
    }

    // №8: функция, которая возвращает безопасный хеш SHA-256 для данной строки.
    public static String hash(String s, String s2) {
        if(s2.equals("--custom")) {
            return customHashFunc(Integer.parseInt(s));
        }
        try {
            return toHexString(getSHA(s));
        } catch(NoSuchAlgorithmException e) {
            System.out.println("Exception thrown for incorrect algorithm: " + e);
        }
        return "You should never see this message. Whatever happened - good luck.";
    }

    //кастомная хэш функция
    private static String customHashFunc(int n) {
        int dn = (n % 95);
        if(dn < 10) return "0"+Integer.toString(dn);
        return Integer.toString(dn);
    }

    //SHA-256
    private static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    //преобразование в 16 ричную строку
    private static String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Adding zeroes for consistent length
        while (hexString.length() < 64)
        {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    // №9: функция, принимающая строку и возвращающая строку с правильным регистром для заголовков в серии Игра Престолов
    public static String correctTitle(String s) {
        // Убираем пробелы
        String ss[] = s.split(" ");
        String buffer = "";
        // в цикле если встречаем слова and, the, of, in, оставляем строчными и добавляем пробелы
        for(int i = 0; i < ss.length; i++) {
            String temp = ss[i].toLowerCase();
            if(temp.equals("of") || temp.equals("the") || temp.equals("and") || temp.equals("in")) {
                buffer = buffer.concat(temp);
            } else {
                // создаем маркер первого прохода цикла
                boolean first = true;
                String buffer2 = "";
                for(int j = 0; j < temp.length(); j++) {
                    char c = temp.charAt(j);
                    if(first) {
                        // при первом проходе получаем значение буффера и меняем маркер, при последующих проходах, меняем буквы на нкжные
                        first = false;
                        buffer2 = buffer2.concat(Character.toString(c-32));
                    } else {
                        buffer2 = buffer2.concat(Character.toString(c));
                    }
                }
                buffer = buffer.concat(buffer2);
            }
            // добавляем пробелы
            buffer = buffer.concat(" ");
        }
        return buffer;
    }

    // №10: функция, которая принимает целое число n и возвращает "недопустимое", если n - не является центрированным шестиугольным число
    // или его иллюстрацией в виде многострочной прямоугольной строки в противном случае
    public static String hexLattice(int n) {
        if(n == 1) return "o";
        int cside = 0;
        int sum = 1;
        while (sum < n) {
            cside++;
            sum = sum + 6*(cside-1);
        }
        if(sum == n) {
            String s = "";
            System.out.println(cside);
            for(int i = cside; i < cside*2; i++) {
                for(int j = 0; j < cside*2-i; j++) {
                    s = s.concat(" ");
                }
                for(int j = 0; j < i; j++) {
                    s = s.concat("o ");
                }
                s = s.concat("\n");
            }
            for(int i = cside*2-2; i > cside-1; i--) {
                for(int j = 0; j < cside*2-i; j++) {
                    s = s.concat(" ");
                }
                for(int j = 0; j < i; j++) {
                    s = s.concat("o ");
                }
                s = s.concat("\n");
            }
            return s;
        }
        return "Invalid";
    }

    //Преобразуем массив строк в массив чисел
    protected static int[] strArrToIntArr(String array[]) {
        int nums[] = new int[array.length];
        for(int i = 0; i < array.length; i++) {
            nums[i] = Integer.parseInt(array[i]);
        }
        return nums;
    }

    //преобрзауем массив строк в массив дробных чисел
    protected static double[] strArrToDoubleArr(String array[]) {
        double nums[] = new double[array.length];
        for(int i = 0; i < array.length; i++) {
            nums[i] = Double.parseDouble(array[i]);
        }
        return nums;
    }

    //выводим массив чисел
    protected static void displayInts(int[] arr) {
        for(int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }

    //Выводим список строк
    protected static void displayStringList(List<String> arr) {
        for(int i = 0; i < arr.size(); i++) {
            System.out.println(arr.get(i));
        }
    }
}

