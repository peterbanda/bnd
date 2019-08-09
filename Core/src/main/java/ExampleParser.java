import com.bnd.core.fsm.CharParser;
import com.bnd.core.fsm.FiniteStateMachineException;

public class ExampleParser {

	enum State {
		Initial,
		LeftRead,
	};

	public static CharParser<State> createParser() {
		CharParser<State> parser = new CharParser<State>(false);

		parser.addTransition(State.Initial, State.LeftRead, '(');
		parser.addTransition(State.LeftRead, State.Initial, ')');
		parser.addTransition(State.LeftRead, State.LeftRead, CharParser.ALPHA_NUMERIC_CHARS);

		parser.setStartState(State.Initial);
		parser.addAcceptState(State.Initial);		

		return parser;
	}

	public static final void main(String args[]) {
		// define input string
		final String inputString = "(ab0)(fd)";
//		final String inputString = "((a0x)(fd)";
//		final String inputString = "(a0x)(fd";

		// create parser
		CharParser<State> parser = createParser();

		try {
			// parse
			parser.parse(inputString);
			System.out.println("Parsing successful");
		} catch (FiniteStateMachineException e) {
			System.out.println("Error occurred: " + e.getMessage());
		}
	}
}