import java.util.Arrays;

public class UPNStack {

    //only accepts Z values
    int[] stack = new int[0];

    public void push(int value) {
        int[] newStack = new int[stack.length + 1];

        for (int i = 0; i < stack.length; i++) {
            newStack[i] = stack[i];
        }

        newStack[newStack.length - 1] = value;

        this.stack = newStack;
    }


    public void pushMany(int... values) {
        for (var c : values) {
            push(c);
        }
    }

    public Integer pop() {

        if (isEmpty()) {
            return null;
        }


        Integer last = stack[stack.length-1];

        stack = Arrays.copyOfRange(stack, 0, stack.length-1);

        return last;
    }

    public boolean isEmpty() {
        return stack.length == 0;
    }

    public int size() {
        return stack.length;
    }

    @Override
    public String toString() {
        return Arrays.toString(stack);
    }


}
