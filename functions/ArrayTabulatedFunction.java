package functions;

public class ArrayTabulatedFunction implements TabulatedFunction {
    private static final int DEFAULT_CAPACITY = 16;
    private FunctionPoint[] points;
    private int pointsCount;
    
    private static final double EPSILON = 1e-10;

    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) 
            throws IllegalArgumentException {
        
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница (" + leftX + 
                    ") должна быть меньше правой (" + rightX + ")");
        }
        
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2, получено: " + pointsCount);
        }
        
        initArrays(Math.max(pointsCount * 2, DEFAULT_CAPACITY));
        this.pointsCount = pointsCount;

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, 0);
        }
    }

    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) 
            throws IllegalArgumentException {
        
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница (" + leftX + 
                    ") должна быть меньше правой (" + rightX + ")");
        }
        
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2, получено: " + values.length);
        }
        
        initArrays(Math.max(values.length * 2, DEFAULT_CAPACITY));
        this.pointsCount = values.length;

        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, values[i]);
        }
    }

    private void initArrays(int capacity) {
        points = new FunctionPoint[capacity];
        for (int i = 0; i < capacity; i++) {
            points[i] = new FunctionPoint();
        }
    }

    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    public double getRightDomainBorder() {
        return points[pointsCount - 1].getX();
    }

    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        for (int i = 0; i < pointsCount - 1; i++) {
            double x1 = points[i].getX();
            double x2 = points[i + 1].getX();
            
            if (x >= x1 - EPSILON && x <= x2 + EPSILON) {
                if (Math.abs(x - x1) < EPSILON) return points[i].getY();
                if (Math.abs(x - x2) < EPSILON) return points[i + 1].getY();
                
                double y1 = points[i].getY();
                double y2 = points[i + 1].getY();
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }
        
        return Double.NaN;
    }

    public int getPointsCount() {
        return pointsCount;
    }

    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, 0, pointsCount - 1);
        }
        return new FunctionPoint(points[index]);
    }

    public void setPoint(int index, FunctionPoint point) 
            throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, 0, pointsCount - 1);
        }
        
        if (index > 0 && point.getX() <= points[index - 1].getX() + EPSILON) {
            throw new InappropriateFunctionPointException(
                point.getX(), points[index - 1].getX(), points[index + 1].getX());
        }
        
        if (index < pointsCount - 1 && point.getX() >= points[index + 1].getX() - EPSILON) {
            throw new InappropriateFunctionPointException(
                point.getX(), points[index - 1].getX(), points[index + 1].getX());
        }

        points[index].setX(point.getX());
        points[index].setY(point.getY());
    }

    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, 0, pointsCount - 1);
        }
        return points[index].getX();
    }

    public void setPointX(int index, double x) 
            throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, 0, pointsCount - 1);
        }
        
        if (index > 0 && x <= points[index - 1].getX() + EPSILON) {
            throw new InappropriateFunctionPointException(
                x, points[index - 1].getX(), points[index + 1].getX());
        }
        
        if (index < pointsCount - 1 && x >= points[index + 1].getX() - EPSILON) {
            throw new InappropriateFunctionPointException(
                x, points[index - 1].getX(), points[index + 1].getX());
        }

        points[index].setX(x);
    }

    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, 0, pointsCount - 1);
        }
        return points[index].getY();
    }

    public void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, 0, pointsCount - 1);
        }
        points[index].setY(y);
    }

    public void deletePoint(int index) 
            throws FunctionPointIndexOutOfBoundsException, IllegalStateException {
        
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, 0, pointsCount - 1);
        }
        
        if (pointsCount <= 2) {
            throw new IllegalStateException("Невозможно удалить точку: минимальное количество точек - 2");
        }
        
        if (index < pointsCount - 1) {
            System.arraycopy(points, index + 1, points, index, pointsCount - index - 1);
        }
        
        points[pointsCount - 1] = new FunctionPoint();
        pointsCount--;
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        int insertIndex = 0;
        while (insertIndex < pointsCount && point.getX() > points[insertIndex].getX() + EPSILON) {
            insertIndex++;
        }

        if (insertIndex < pointsCount && 
            Math.abs(point.getX() - points[insertIndex].getX()) < EPSILON) {
            throw new InappropriateFunctionPointException(point.getX());
        }

        if (pointsCount == points.length) {
            int newCapacity = points.length * 2;
            FunctionPoint[] newArray = new FunctionPoint[newCapacity];
            System.arraycopy(points, 0, newArray, 0, pointsCount);
            
            for (int i = pointsCount; i < newCapacity; i++) {
                newArray[i] = new FunctionPoint();
            }
            
            points = newArray;
        }

        if (insertIndex < pointsCount) {
            System.arraycopy(points, insertIndex, points, insertIndex + 1, pointsCount - insertIndex);
        }
        
        points[insertIndex] = new FunctionPoint(point);
        pointsCount++;
    }

    public void printPoints() {
        System.out.println("Табулированная функция (" + pointsCount + " точек):");
        for (int i = 0; i < pointsCount; i++) {
            System.out.printf("  [%d]: (%.2f, %.2f)%n", i, points[i].getX(), points[i].getY());
        }
    }
}