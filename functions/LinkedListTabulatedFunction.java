package functions;

public class LinkedListTabulatedFunction implements TabulatedFunction {
    private class FunctionNode {
        private FunctionPoint point;
        private FunctionNode prev;
        private FunctionNode next;
        
        public FunctionNode(FunctionPoint point) {
            this.point = point;
        }
        
        public FunctionPoint getPoint() {
            return point;
        }
        
        public void setPoint(FunctionPoint point) {
            this.point = point;
        }
        
        public FunctionNode getPrev() {
            return prev;
        }
        
        public void setPrev(FunctionNode prev) {
            this.prev = prev;
        }
        
        public FunctionNode getNext() {
            return next;
        }
        
        public void setNext(FunctionNode next) {
            this.next = next;
        }
    }
    
    private FunctionNode head;      
    private int pointsCount;        
    private FunctionNode lastNode;  
    private int lastIndex;          
    
    private static final double EPSILON = 1e-10;
    
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) 
            throws IllegalArgumentException {
        
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница (" + leftX + 
                    ") должна быть меньше правой (" + rightX + ")");
        }
        
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2, получено: " + pointsCount);
        }
        
        initList();
        this.pointsCount = pointsCount;
        
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            FunctionNode newNode = addNodeToTail();
            newNode.setPoint(new FunctionPoint(x, 0));
        }
        
        initCache();
    }
    
    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) 
            throws IllegalArgumentException {
        
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница (" + leftX + 
                    ") должна быть меньше правой (" + rightX + ")");
        }
        
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2, получено: " + values.length);
        }
        
        initList();
        this.pointsCount = values.length;
        
        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = leftX + i * step;
            FunctionNode newNode = addNodeToTail();
            newNode.setPoint(new FunctionPoint(x, values[i]));
        }
        
        initCache();
    }
    
    private void initList() {
        head = new FunctionNode(null);
        head.setPrev(head);
        head.setNext(head);
        pointsCount = 0;
    }
    
    private void initCache() {
        if (pointsCount > 0) {
            lastNode = head.getNext();
            lastIndex = 0;
        } else {
            lastNode = head;
            lastIndex = -1;
        }
    }
    
    private FunctionNode getNodeByIndex(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, 0, pointsCount - 1);
        }
        
        if (pointsCount == 0) {
            return head;
        }
        
        if (lastNode != null && lastNode != head) {
            int distanceFromLast = Math.abs(index - lastIndex);
            
            if (index == lastIndex) {
                return lastNode;
            }
            
            if (distanceFromLast <= index && distanceFromLast <= pointsCount - 1 - index) {
                return moveFromNode(lastNode, lastIndex, index);
            }
        }
        
        if (index <= pointsCount / 2) {
            lastNode = moveFromNode(head.getNext(), 0, index);
        } else {
            lastNode = moveFromNode(head.getPrev(), pointsCount - 1, index);
        }
        
        lastIndex = index;
        return lastNode;
    }
    
    private FunctionNode moveFromNode(FunctionNode startNode, int startIndex, int targetIndex) {
        FunctionNode currentNode = startNode;
        int currentIndex = startIndex;
        
        if (targetIndex > currentIndex) {
            while (currentIndex < targetIndex) {
                currentNode = currentNode.getNext();
                currentIndex++;
            }
        } else if (targetIndex < currentIndex) {
            while (currentIndex > targetIndex) {
                currentNode = currentNode.getPrev();
                currentIndex--;
            }
        }
        
        return currentNode;
    }
    
    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(null);
        
        FunctionNode tail = head.getPrev();
        
        newNode.setPrev(tail);
        newNode.setNext(head);
        
        tail.setNext(newNode);
        head.setPrev(newNode);
        
        pointsCount++;
        return newNode;
    }
    
    private FunctionNode addNodeByIndex(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index > pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, 0, pointsCount);
        }
        
        if (index == pointsCount) {
            return addNodeToTail();
        }
        
        FunctionNode nodeAtIndex = getNodeByIndex(index);
        FunctionNode newNode = new FunctionNode(null);
        
        FunctionNode prevNode = nodeAtIndex.getPrev();
        newNode.setPrev(prevNode);
        newNode.setNext(nodeAtIndex);
        
        prevNode.setNext(newNode);
        nodeAtIndex.setPrev(newNode);
        
        pointsCount++;
        
        if (index <= lastIndex) {
            lastIndex++;
        }
        
        return newNode;
    }
    
    private FunctionNode deleteNodeByIndex(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, 0, pointsCount - 1);
        }
        
        FunctionNode nodeToDelete = getNodeByIndex(index);
        
        FunctionNode prevNode = nodeToDelete.getPrev();
        FunctionNode nextNode = nodeToDelete.getNext();
        
        prevNode.setNext(nextNode);
        nextNode.setPrev(prevNode);
        
        nodeToDelete.setPrev(null);
        nodeToDelete.setNext(null);
        
        pointsCount--;
        
        if (nodeToDelete == lastNode) {
            lastNode = nextNode;
            if (lastNode == head) {
                if (pointsCount > 0) {
                    lastNode = head.getNext();
                    lastIndex = 0;
                } else {
                    lastNode = head;
                    lastIndex = -1;
                }
            }
        } else if (index < lastIndex) {
            lastIndex--;
        }
        
        return nodeToDelete;
    }
    
    public double getLeftDomainBorder() {
        if (pointsCount == 0) {
            return Double.NaN;
        }
        return head.getNext().getPoint().getX();
    }
    
    public double getRightDomainBorder() {
        if (pointsCount == 0) {
            return Double.NaN;
        }
        return head.getPrev().getPoint().getX();
    }
    
    public double getFunctionValue(double x) {
        if (pointsCount == 0) {
            return Double.NaN;
        }
        
        double leftBorder = getLeftDomainBorder();
        double rightBorder = getRightDomainBorder();
        
        if (x < leftBorder - EPSILON || x > rightBorder + EPSILON) {
            return Double.NaN;
        }
        
        FunctionNode currentNode = head.getNext();
        while (currentNode != head) {
            FunctionNode nextNode = currentNode.getNext();
            if (nextNode == head) break; 
            
            double x1 = currentNode.getPoint().getX();
            double x2 = nextNode.getPoint().getX();
            
            if (x >= x1 - EPSILON && x <= x2 + EPSILON) {
                if (Math.abs(x - x1) < EPSILON) {
                    return currentNode.getPoint().getY();
                }
                if (Math.abs(x - x2) < EPSILON) {
                    return nextNode.getPoint().getY();
                }
                
                double y1 = currentNode.getPoint().getY();
                double y2 = nextNode.getPoint().getY();
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
            
            currentNode = nextNode;
        }
        
        return Double.NaN;
    }
    
    public int getPointsCount() {
        return pointsCount;
    }
    
    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException {
        FunctionNode node = getNodeByIndex(index);
        return new FunctionPoint(node.getPoint());
    }
    
    public void setPoint(int index, FunctionPoint point) 
        throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        
        FunctionNode node = getNodeByIndex(index);
        
        double newX = point.getX();
        double nodeX = node.getPoint().getX();
        
        if (Math.abs(newX - nodeX) < EPSILON) {
            node.getPoint().setY(point.getY());
            return;
        }
        
        FunctionNode prevNode = node.getPrev();
        FunctionNode nextNode = node.getNext();
        
        if (prevNode != head) {
            double prevX = prevNode.getPoint().getX();
            if (newX <= prevX + EPSILON) {
                throw new InappropriateFunctionPointException(
                    "Новая координата X (" + newX + ") должна быть больше предыдущей (" + prevX + ")");
            }
        }
        
        if (nextNode != head) {
            double nextX = nextNode.getPoint().getX();
            if (newX >= nextX - EPSILON) {
                throw new InappropriateFunctionPointException(
                    "Новая координата X (" + newX + ") должна быть меньше следующей (" + nextX + ")");
            }
        }
        
        node.getPoint().setX(newX);
        node.getPoint().setY(point.getY());
    }
    
    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException {
        return getNodeByIndex(index).getPoint().getX();
    }
    
    public void setPointX(int index, double x) 
            throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        
        FunctionNode node = getNodeByIndex(index);
        
        double currentX = node.getPoint().getX();
        if (Math.abs(x - currentX) < EPSILON) {
            return; // X не изменился
        }
        
        FunctionNode prevNode = node.getPrev();
        FunctionNode nextNode = node.getNext();
        
        if (prevNode != head) {
            double prevX = prevNode.getPoint().getX();
            if (x <= prevX + EPSILON) {
                throw new InappropriateFunctionPointException(
                    "Новая координата X (" + x + ") должна быть больше предыдущей (" + prevX + ")");
            }
        }
        
        if (nextNode != head) {
            double nextX = nextNode.getPoint().getX();
            if (x >= nextX - EPSILON) {
                throw new InappropriateFunctionPointException(
                    "Новая координата X (" + x + ") должна быть меньше следующей (" + nextX + ")");
            }
        }
        
        node.getPoint().setX(x);
    }
    
    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException {
        return getNodeByIndex(index).getPoint().getY();
    }
    
    public void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException {
        getNodeByIndex(index).getPoint().setY(y);
    }
    
    public void deletePoint(int index) 
            throws FunctionPointIndexOutOfBoundsException, IllegalStateException {
        
        if (pointsCount <= 2) {
            throw new IllegalStateException("Невозможно удалить точку: минимальное количество точек - 2");
        }
        
        deleteNodeByIndex(index);
    }
    
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        if (pointsCount == 0) {
            FunctionNode newNode = addNodeToTail();
            newNode.setPoint(new FunctionPoint(point));
            initCache();
            return;
        }
        
        double newX = point.getX();
        
        double leftBorder = getLeftDomainBorder();
        double rightBorder = getRightDomainBorder();
        
        if (newX < leftBorder - EPSILON) {
            // Добавляем в начало
            FunctionNode newNode = addNodeByIndex(0);
            newNode.setPoint(new FunctionPoint(point));
            return;
        }
        
        if (newX > rightBorder + EPSILON) {
            FunctionNode newNode = addNodeToTail();
            newNode.setPoint(new FunctionPoint(point));
            return;
        }
        
        int insertIndex = 0;
        FunctionNode currentNode = head.getNext();
        
        while (currentNode != head && newX > currentNode.getPoint().getX() + EPSILON) {
            currentNode = currentNode.getNext();
            insertIndex++;
        }
        
        if (currentNode != head && 
            Math.abs(newX - currentNode.getPoint().getX()) < EPSILON) {
            throw new InappropriateFunctionPointException(
                "Точка с координатой X = " + newX + " уже существует");
        }
        
        FunctionNode newNode = addNodeByIndex(insertIndex);
        newNode.setPoint(new FunctionPoint(point));
    }
    
    public void printPoints() {
        System.out.println("Табулированная функция (связный список, " + pointsCount + " точек):");
        
        if (pointsCount == 0) {
            System.out.println("  Функция не содержит точек");
            return;
        }
        
        FunctionNode currentNode = head.getNext();
        int index = 0;
        
        while (currentNode != head) {
            double x = currentNode.getPoint().getX();
            double y = currentNode.getPoint().getY();
            System.out.printf("  [%d]: (%.4f, %.4f)%n", index, x, y);
            currentNode = currentNode.getNext();
            index++;
        }
    }
    
    public void checkListIntegrity() {
        if (head == null) {
            System.out.println("ERROR: head is null");
            return;
        }
        
        System.out.println("Проверка целостности списка:");
        System.out.println("  pointsCount = " + pointsCount);
        System.out.println("  head.prev = " + (head.getPrev() == head ? "head (OK)" : "ERROR"));
        System.out.println("  head.next = " + (head.getNext() == head ? "head (OK для пустого списка)" : "узел"));
        
        if (pointsCount > 0) {
            FunctionNode first = head.getNext();
            if (first.getPrev() != head) {
                System.out.println("  ERROR: first.prev != head");
            }
            
            FunctionNode last = head.getPrev();
            if (last.getNext() != head) {
                System.out.println("  ERROR: last.next != head");
            }
            
            int count = 0;
            FunctionNode current = head.getNext();
            while (current != head) {
                count++;
                current = current.getNext();
            }
            
            if (count != pointsCount) {
                System.out.println("  ERROR: counted nodes = " + count + ", but pointsCount = " + pointsCount);
            } else {
                System.out.println("  OK: counted nodes matches pointsCount");
            }
        }
    }
}