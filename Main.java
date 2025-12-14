import functions.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Тестирование табулированных функций ===\n");
        
        testArrayFunction();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        testLinkedListFunction();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        testExceptions();
    }
    
    private static void testArrayFunction() {
        System.out.println("1. Тестирование ArrayTabulatedFunction:");
        
        try {
            TabulatedFunction parabola = new ArrayTabulatedFunction(-2, 2, 5);
            
            for (int i = 0; i < parabola.getPointsCount(); i++) {
                double x = parabola.getPointX(i);
                parabola.setPointY(i, x * x);
            }
            
            parabola.printPoints(); 
            
            // Тестируем вычисления
            System.out.println("\n   Вычисления:");
            System.out.printf("   f(0.5) = %.4f%n", parabola.getFunctionValue(0.5));
            System.out.printf("   f(3) = %s%n", parabola.getFunctionValue(3));
            
            // Добавляем точку
            parabola.addPoint(new FunctionPoint(1.5, 2.25));
            System.out.println("\n   После добавления точки (1.5, 2.25):");
            parabola.printPoints(); // БЫЛО printPoint()
            
        } catch (Exception e) {
            System.out.println("   Ошибка: " + e.getMessage());
        }
    }
    
    private static void testLinkedListFunction() {
        System.out.println("2. Тестирование LinkedListTabulatedFunction:");
        
        try {
            double[] sinValues = {0, 0.707, 1, 0.707, 0, -0.707, -1, -0.707, 0};
            TabulatedFunction sinFunc = new LinkedListTabulatedFunction(0, Math.PI * 2, sinValues);
            
            sinFunc.printPoints(); 
            
            System.out.println("\n   Вычисления:");
            System.out.printf("   f(%.2f) = %.4f%n", Math.PI/4, sinFunc.getFunctionValue(Math.PI/4));
            System.out.printf("   f(%.2f) = %.4f%n", Math.PI, sinFunc.getFunctionValue(Math.PI));
            
            sinFunc.deletePoint(3);
            System.out.println("\n   После удаления точки с индексом 3:");
            sinFunc.printPoints(); 
            
        } catch (Exception e) {
            System.out.println("   Ошибка: " + e.getMessage());
        }
    }
    
    private static void testExceptions() {
        System.out.println("3. Тестирование исключений:");
        
        System.out.println("\n   a) Некорректные параметры конструктора:");
        try {
            TabulatedFunction badFunc = new ArrayTabulatedFunction(10, 5, 5);
        } catch (IllegalArgumentException e) {
            System.out.println("   Поймано: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        
        System.out.println("\n   b) Выход за границы массива:");
        try {
            TabulatedFunction func = new ArrayTabulatedFunction(0, 10, 5);
            func.getPoint(10);
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.out.println("   Поймано: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        
        System.out.println("\n   c) Нарушение порядка точек:");
        try {
            TabulatedFunction func = new ArrayTabulatedFunction(0, 10, 5);
            func.setPoint(2, new FunctionPoint(8, 0)); // Должно быть между 5 и 7.5
        } catch (InappropriateFunctionPointException e) {
            System.out.println("   Поймано: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        
        System.out.println("\n   d) Удаление при недостаточном количестве точек:");
        try {
            TabulatedFunction func = new ArrayTabulatedFunction(0, 10, 3);
            func.deletePoint(0);
            func.deletePoint(0); // Осталась 1 точка
        } catch (IllegalStateException e) {
            System.out.println("   Поймано: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        
        System.out.println("\n   e) Добавление точки с существующим X:");
        try {
            TabulatedFunction func = new ArrayTabulatedFunction(0, 10, 3);
            func.addPoint(new FunctionPoint(5, 100));
        } catch (InappropriateFunctionPointException e) {
            System.out.println("   Поймано: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        
        System.out.println("\n   f) Тест с LinkedList:");
        try {
            TabulatedFunction linkedFunc = new LinkedListTabulatedFunction(0, 5, 4);
            System.out.println("   LinkedList функция создана успешно");
            linkedFunc.printPoints();
        } catch (Exception e) {
            System.out.println("   Ошибка: " + e.getMessage());
        }
    }
}