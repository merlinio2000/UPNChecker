public class UPNChecker {

    private static boolean isZ(Character character) {
        if (character == null) return false;
        int asciiCode = character.charValue();
        return Character.isDigit(character);
    }

    private static boolean isO(Character character) {
        if (character == null) return false;
        return (character == '*' || character == '+');
    }

    // requires word to be trimmed externally
    public static State transition(State currentState, String word, UPNStack stack) {

        System.out.println("currentState = " + currentState + ", word = " + word + ", stack = " + stack);

        Character wordChar;
        if (!word.isEmpty()) {
             wordChar = word.charAt(0);
        } else {
            wordChar = null;
        }
        Integer stackVal = stack.pop();

        switch (currentState) {
            case Q0 -> {
                if (isZ(wordChar) && stackVal == null) {
                    stack.push(Character.getNumericValue(wordChar));
                    return State.Q1;
                }
            }
            case Q1 -> {
                if (isZ(wordChar)) {
                    stack.pushMany(stackVal, Character.getNumericValue(wordChar));
                    return State.Q2;
                } else if (wordChar == null) {
                    stack.push(stackVal);
                    return State.Q1;
                }
            }
            case Q2 -> {
                if (isZ(wordChar)) {
                    stack.pushMany(stackVal, Character.getNumericValue(wordChar));
                    return State.Q2;
                } else if (isO(wordChar)) {
                    Integer secondOperand = stack.pop();
                    switch (wordChar) {
                        case '+' -> stack.push(stackVal + secondOperand);
                        case '*' -> stack.push(stackVal * secondOperand);
                        default -> throw new RuntimeException("Unexpected operation " + wordChar);
                    }

                    return State.Q3;
                }
            }
            case Q3 -> {
                if (isO(wordChar)) {
                    Integer secondOperand = stack.pop();

                    if (secondOperand == null) {
                        return State.GARBAGE;
                    }

                    switch (wordChar) {
                        case '+' -> stack.push(stackVal + secondOperand);
                        case '*' -> stack.push(stackVal * secondOperand);
                        default -> throw new RuntimeException("Unexpected operation " + wordChar);
                    }
                    return State.Q3;
                } else if (isZ(wordChar)) {
                    stack.pushMany(stackVal, Character.getNumericValue(wordChar));
                    return State.Q2;
                } else if (wordChar == null) {
                    stack.push(stackVal);
                    return State.Q1;
                }
            }
            default -> System.err.println("Didnt expect state " + currentState.name());
        }

        return State.GARBAGE;
    }


    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Wrong number of arguments");
            System.exit(1);
        }



        String word = args[0];
        State state = State.Q0;
        UPNStack stack = new UPNStack();


        while (!(word.isEmpty() && state == State.Q1)) {
            State newState = transition(state, word, stack);

            if (newState == State.GARBAGE) {
                state = newState;
                break;
            } else {
                state = newState;
                if (!word.isEmpty()) {
                    word = word.substring(1);
                }
            }
        }

        System.out.println("Ended in state " + state.name());

        if (state.isAccepting) {
            System.out.println("This state is accepting!");
            System.out.println("Result is " + stack.pop());
        } else  {
            System.err.println("This state is not accepting!");
        }
    }


    enum State {
        Q0, Q1(true), Q2, Q3, GARBAGE;

        final boolean isAccepting;

        State(boolean isAccepting) {
            this.isAccepting = isAccepting;
        }

        State() {
            this(false);
        }

    }


}
