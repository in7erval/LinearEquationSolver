package solver;
import java.util.*;
import java.io.*;

public class Main {

    static class Complex {
        double x;
        double y;

        public Complex(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public Complex(Complex c) {
            this.x = c.x;
            this.y = c.y;
        }

        public void multiply(Complex c) {
            double prevX = this.x;
            double prevY = this.y;

            this.x = prevX * c.x - prevY * c.y;
            this.y = prevX * c.y + prevY * c.x;
        }

        public void divide(Complex c) {
            if (this.x == c.x && this.y == c.y) {
                this.x = 1;
                this.y = 0;
            } else {
                double prevX = this.x;
                double prevY = this.y;
                double div = c.x * c.x + c.y * c.y;

                this.x = (prevX * c.x + prevY * c.y) / div;
                this.y = (prevY * c.x - prevX * c.y) / div;
            }
        }

        public void substract(Complex c) {
            this.x -= c.x;
            this.y -= c.y;
        }

        public void sum(Complex c) {
            this.x += c.x;
            this.y += c.y;
        }

        public boolean isZero() {
            return this.x == 0 && this.y == 0;
        }

        public boolean isOne() {
            return this.x == 1 && this.y == 0;
        }

        public boolean isNegativeOne() {
            return this.x == -1 && this.y == 0;
        }

        public void negative() {
            this.x *= -1;
            this.y *= -1;
        }

        public Complex clone() {
            return new Complex(this.x, this.y);
        }
    }

    public static Complex parseComplex (String str) {
        int i = 0;
        if (str.charAt(i) == '-') {
            i++;
        }
        while (i < str.length() && (str.charAt(i) >= '0' && str.charAt(i) <= '9' || str.charAt(i) == '.')) {
            i++;
        }
        if (i == str.length()) { //...only real
            return new Complex(Double.parseDouble(str), 0);
        } else if (str.charAt(i) == 'i' && i == str.length() - 1) { //...only imaginary
            if (i == 0 || (i == 1 && str.charAt(i - 1) == '-')) {
                return (i == 0) ? new Complex(0, 1) : new Complex(0, -1);
            }
            return new Complex(0, Double.parseDouble(str.split("i")[0]));
        } else if (str.charAt(i) == '+' || str.charAt(i)== '-') { //...real+imaginary
            String real = str.substring(0, i);
            String imaginary;
            if (str.charAt(i + 1) == 'i') {
                imaginary = (str.charAt(i) == '-') ? "-1" : "1";
            } else {
                imaginary = (str.charAt(i) == '-') ? str.substring(i, str.length() - 1) : str.substring(i + 1, str.length() - 1);
            }
            return new Complex(Double.parseDouble(real), Double.parseDouble(imaginary));
        } else { //......Обработай!!!!
            return null;
        }
    }

    static class Row {
        int len;
        Complex[] element;

        public Row(String[] str) {
            len  = str.length;
            element = new Complex[len];
            for (int i = 0; i < len; i++) {
                element[i] = parseComplex(str[i]);
            }
        }

        public Row() {}

        public Row(Row x) {
            int len = x.getLen();
            Complex[] elem = new Complex[len];
            for (int i = 0; i < len; i++) {
                elem[i] = x.element[i].clone();
            }
            this.element = elem;
            this.len = x.len;
        }

        public Row(int len, Complex[] row) {
            this.element = row.clone();
            this.len = len;
        }

        public void multipleBy(Complex x) {
            for (int i = 0; i < len; i++) {
                this.element[i].multiply(x);
            }
        }

        public void negative() {
            for (int i = 0; i < len; i++) {
                this.element[i].negative();
            }
        }

        public void divideBy(Complex x) {
            for (int i = 0; i < len; i++) {
                this.element[i].divide(x);
            }
        }

        public void sumFrom(Row r) {
            Complex[] rowFrom = r.getElement();
            for (int i = 0; i < len; i++) {
                this.element[i].sum(rowFrom[i]);
            }
        }

        public void subtractFrom(Row r) {
            Complex[] rowFrom = r.getElement();
            for (int i = 0; i < len; i++) {
                this.element[i].substract(rowFrom[i]);
            }
        }

        public boolean zeroRow() {
            for (int i = 0; i < len; i++) {
                if (!element[i].isZero()) {
                    return false;
                }
            }
            return true;
        }

        public boolean invalidRow() {
            boolean hasNoZero = true;
            for (int i = 0; i < len - 1; i++) {
                if (!element[i].isZero()) {
                    hasNoZero = false;
                    break;
                }
            }
            return !element[len - 1].isZero() && hasNoZero;
        }



        public Row clone() {
            return new Row(this);
        }

        public void setLen(int len) {
            this.len = len;
        }

        public void setElement(Complex[] row) {
            this.element = row.clone();
        }

        public Complex[] getElement() {
            return element.clone();
        }

        public int getLen() {
            return len;
        }

    }

    static class Matrix {
        public Row[] rows;
        int countRows;
        boolean print;

        public Matrix(Row[] rows, int countRows) {
            this.rows = rows.clone();
            this.countRows = countRows;
            this.print = false;
        }

        public int getCountRows() {
            return countRows;
        }


        public int countZeroRows() {
            int count = 0;
            for (int i = 0; i < countRows; i++) {
                if (rows[i].zeroRow()) {
                    count++;
                }
            }
            return count;
        }

        public void swapRows(int firstRow, int secondRow) {
            Row buf = rows[firstRow];
            rows[firstRow] = rows[secondRow];
            rows[secondRow] = buf;
        }

        public void swapColumns(int firstColumn, int secondColumn) {
            for (int i = 0; i < countRows; i++) {
                Complex buf = rows[i].element[firstColumn];
                rows[i].element[firstColumn] = rows[i].element[secondColumn];
                rows[i].element[secondColumn] = buf;
            }
        }

        public void printMatrix() {
            if (this.print) {
                for (int i = 0; i < countRows; i++) {
                    Complex[] currentRow = rows[i].getElement();
                    int len = rows[i].getLen();
                    for (int j = 0; j < len; j++) {
                        System.out.printf("%10.3f%+.3fi", currentRow[j].x, currentRow[j].y);
                        if (j != len - 1) {
                            System.out.print("\t");
                        }
                    }
                    System.out.println("");
                }
                System.out.println("");
            }
        }

        public void mainPrintMatrix() {
            for (int i = 0; i < countRows; i++) {
                Complex[] currentRow = rows[i].getElement();
                int len = rows[i].getLen();
                for (int j = 0; j < len; j++) {
                    System.out.printf("%10.3f%+.3fi", currentRow[j].x, currentRow[j].y);
                    if (j != len - 1) {
                        System.out.print("\t");
                    }
                }
                System.out.println("");
            }
            System.out.println("");
        }
    }

    static class LinearEquation {
        public Matrix matrix;
        int variables;
        int equations;
        int check;

        public LinearEquation(Matrix m, int v, int e) {
            matrix = m;
            variables = v;
            equations = e;
        }

        public int checkInvalidRows() {
            int countRows = matrix.getCountRows();

            for (int i = 0; i < countRows; i++) {
                if (matrix.rows[i].invalidRow()) {
                    return 2; //...no solutions;
                }
            }
            return 0;
        }

        public int checkRows() {
            int zeroRows = matrix.countZeroRows();
            int countRows = matrix.getCountRows();

            for (int i = 0; i < countRows; i++) {
                if (matrix.rows[i].invalidRow()) {
                    return 2; //...no solutions;
                }
            }
            if (countRows - zeroRows < variables) {
                return 1;//...infinitely many solutions
            }
            if (countRows - zeroRows > variables) {
                return 2; //...no solutions
            }
            return 0;
        }

        public int solve() {
            int countRows = matrix.getCountRows();

            for (int i = 0; i < countRows; i++) {
                if (!matrix.rows[i].element[i].isZero()) {
                    if (!matrix.rows[i].element[i].isOne() && !matrix.rows[i].element[i].isNegativeOne()) {
                        System.out.printf("R%d / %.3f%+.3fi -> R%d\n", i + 1, matrix.rows[i].element[i].x, matrix.rows[i].element[i].y ,i + 1);
                        matrix.rows[i].divideBy(new Complex(matrix.rows[i].element[i]));
                        matrix.printMatrix();
                    } else if (matrix.rows[i].element[i].isNegativeOne()) {
                        matrix.rows[i].negative();
                        System.out.printf("R%d -> -R%d\n", i + 1, i + 1);
                        matrix.printMatrix();
                    }
                } else {
                    boolean swap = false;
                    for (int j = i; j < countRows; j++) {
                        if (!matrix.rows[j].element[i].isZero()) {
                            matrix.swapRows(i, j);
                            System.out.printf("R%d <-> R%d\n", i + 1, j + 1);
                            matrix.printMatrix();
                            swap = true;
                            break;
                        }
                    }
                    if (!swap) {
                        for (int j = i; j < matrix.rows[i].getLen(); j++) {
                            if (!matrix.rows[i].element[j].isZero()) {
                                matrix.swapColumns(i, j);
                                System.out.printf("C%d <-> C%d\n", i + 1, j + 1);
                                matrix.printMatrix();
                                //....Don't forget to remember it!
                                swap = true;
                                break;
                            }
                        }
                    }
                    if (!swap) {
                        for (int k = i + 1; k < countRows; k++) {
                            for (int m = i + 1; m < matrix.rows[k].getLen(); m++) {
                                if (!matrix.rows[k].element[m].isZero()) {
                                    matrix.swapRows(k, i);
                                    matrix.swapColumns(m, i);
                                    System.out.printf("R%d <-> R%d && C%d <-> C%d\n", k + 1, i + 1, m + 1, i + 1);
                                    matrix.printMatrix();
                                    //...Don't forget to remember it!
                                    swap = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!swap) {
                        return 1;
                    }
                    i--;
                    continue;
                }
                for (int j = 0; j < countRows; j++) {
                    if (j != i && !matrix.rows[j].element[i].isZero()) {
                        if (matrix.rows[j].element[i] != matrix.rows[i].element[i]) {
                            System.out.printf("R%d - R%d * %.3f%+.3fi -> R%d\n", j + 1, i + 1, matrix.rows[j].element[i].x, matrix.rows[j].element[i].y, j + 1);
                            Row r = matrix.rows[i].clone();
                            r.multipleBy(new Complex(matrix.rows[j].element[i]));
                            matrix.rows[j].subtractFrom(r);
                        } else {
                            System.out.printf("R%d - R%d -> R%d\n", j + 1, i + 1, j + 1);
                            matrix.rows[j].subtractFrom(matrix.rows[i]);
                        }
                        matrix.printMatrix();
                    }
                }
                if (checkInvalidRows() == 2) {
                    return 2;
                }
            }
            return 0;
        }

        public Row saveSolve () {
            this.check = solve();
            if (this.check == 0) {
                this.check = checkRows();
            }
            if (this.check == 0) {
                Complex[] ans = new Complex[matrix.countRows];
                for (int i = 0; i < matrix.countRows; i++) {
                    for (int j = 0; j < matrix.rows[i].getLen() - 1; j++) {
                        if (matrix.rows[i].getElement()[j].isOne())
                            ans[j] = matrix.rows[i].getElement()[matrix.rows[i].getLen() - 1];
                    }
                }
                return new Row(matrix.countRows, ans);
            } else {
                return null;
            }
        }

        public int getCheck() {
            return check;
        }
    }

    public static LinearEquation equationInit(String filename) {
        File file = new File(filename);
        try {
            Scanner scan = new Scanner(file);
            String line;
            String[] str;

            System.out.println("Contains of file:");
            line = scan.nextLine();
            System.out.println(line);
            str = line.split("[ ]+");
            int variables = Integer.parseInt(str[0]);
            int equations = Integer.parseInt(str[1]);
            int countRows = 0;
            Row[] r = new Row[equations];
            Row row;
             for (int i = 0; i < equations; i++) {
                 line = scan.nextLine();
                 System.out.println(line);
                 str = line.split("[ ]+");
                 row = new Row(str);
                 if (!row.zeroRow()) {
                     r[countRows++] = row;
                 }
            }
            Matrix m = new Matrix(r, countRows);
            return new LinearEquation(m, variables, countRows);
        } catch (FileNotFoundException e) {
            System.out.println("File not found! ERROR: " + e.getMessage());
        }
        return null;
    }


    public static void equation(String[] file) {
        try {
            LinearEquation l = equationInit(file[0]);
            System.out.println(">>>> Your matrix: <<<<");
            l.matrix.mainPrintMatrix();
            Row answer = l.saveSolve();
            System.out.println(">>>> End of equations <<<<");
            l.matrix.mainPrintMatrix();
            PrintWriter p = new PrintWriter(file[1]);
            if (answer != null) {
                for (Complex x : answer.getElement()) {
                    p.printf("%.3f%+.3fi\n", x.x, x.y);
                }
            } else {
                if (l.getCheck() == 1) {
                    p.println("Infinitely many solutions");
                    System.out.println("Infinitely many solutions");
                } else {
                    p.println("No solutions");
                    System.out.println("No solutions");
                }
            }
            p.close();
            System.out.println("Saved to file out.txt");
        } catch (FileNotFoundException e) {
            System.out.println("File not found! ERROR: " + e.getMessage());
        }
    }

    public static String[] parseArgs(String[] args) {
        if (args.length != 4) {
            return null;
        }
        String[] files = new String[2];
        if (args[0].equals("-in")) {
            if (args[1].equals("-out")) {
                return null;
            }
            files[0] = args[1];
            if (args[2].equals("-out")) {
                if (args[3].equals("-in")) {
                    return null;
                }
                files[1] = args[3];
            }
            return files;
        }
        if (args[0].equals("-out")) {
            if (args[1].equals("-in")) {
                return null;
            }
            files[1] = args[1];
            if (args[2].equals("-in")) {
                if (args[3].equals("-out")) {
                    return null;
                }
                files[0] = args[3];
            }
            return files;
        }
        return null;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java Main.java -in in.txt -out out.txt");
        } else {
            String[] files = parseArgs(args);
            if (files == null) {
                System.out.println("Incorrect arguments!");
            } else {
                equation(files);
            }
        }
        /*
        Complex  c = parseComplex("91+5i");
        System.out.printf("%.3f%+.3fi\n", c.x, c.y);
         */
    }
}
