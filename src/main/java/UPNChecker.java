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

    public static State transition(State currentState, StrRef wRef, UPNStack stack) {

        System.out.println("currentState = " + currentState + ", word = " + wRef.inner + ", stack = " + stack);

        Character wordChar;
        if (wRef.inner.isEmpty()) {
            wordChar = null;
        } else {
            wordChar = wRef.inner.charAt(0);
            wRef.inner = wRef.inner.substring(1);
        }
        boolean lastElem = (stack.size() == 1);

        Integer stackVal = stack.pop();

        switch (currentState) {
            case Q0 -> {
                if (isZ(wordChar) && stackVal == null) {
                    stack.push(Character.getNumericValue(wordChar));
                    return State.Q1;
                }
            }
            case Q1 -> {
                if (isZ(wordChar) && lastElem) {
                    stack.pushMany(stackVal, Character.getNumericValue(wordChar));
                    return State.Q2;
                }
            }
            case Q2 -> {
                if (isZ(wordChar) && !lastElem) {
                    stack.pushMany(stackVal, Character.getNumericValue(wordChar));
                    return State.Q2;
                } else if (isO(wordChar) && !lastElem) {
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
                if (isO(wordChar) && !lastElem) {
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
                } else if (isZ(wordChar) && !lastElem) {
                    stack.pushMany(stackVal, Character.getNumericValue(wordChar));
                    return State.Q2;
                } else if (lastElem) {
                    // we dont read the character of the word in this transition, so add it back to fake it
                    if (wordChar != null)
                        wRef.inner = wordChar + wRef.inner;
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


		StrRef wRef = new StrRef();
		wRef.inner = args[0];
        State state = State.Q0;
        UPNStack stack = new UPNStack();


        while (!(wRef.inner.isEmpty() && state.isAccepting)) {
            state = transition(state, wRef, stack);
            if (state == State.GARBAGE) {
                break;
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
	
	private static class StrRef {
		public String inner;
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
