package com.bnd.core.fsm;

import java.util.*;

import com.bnd.core.Pair;
import com.bnd.core.fsm.action.AddToBufferTransitionAction;
import com.bnd.core.fsm.action.BufferTransitionAction;

public class CharParser<S> extends FiniteStateMachineWithBuffer<S, Character> {

	public static final Set<Character> LOWER_CASE_LETTERS;
	public static final Set<Character> UPPER_CASE_LETTERS;
	public static final Set<Character> LETTERS;
	public static final Set<Character> NUMBERS;
	public static final Set<Character> ALPHA_NUMERIC_CHARS;
	public static final BufferTransitionAction<Character> ADD_CHAR_TO_BUFFER_ACTION = new AddToBufferTransitionAction<Character>();

	static {
		// initialization of the lower case letters
		Set<Character> LOWER_CASE_LETTERS_TEMP = new HashSet<Character>();
		for (char c = 'a'; c <= 'z'; c++) {
			LOWER_CASE_LETTERS_TEMP.add(c);
		}
		LOWER_CASE_LETTERS = Collections.unmodifiableSet(LOWER_CASE_LETTERS_TEMP);

		// initialization of the uppper case letters
		Set<Character> UPPER_CASE_LETTERS_TEMP = new HashSet<Character>();
		for (char c = 'A'; c <= 'Z'; c++) {
			UPPER_CASE_LETTERS_TEMP.add(c);
		}
		UPPER_CASE_LETTERS = Collections.unmodifiableSet(UPPER_CASE_LETTERS_TEMP);

		// initialization of the numbers
		Set<Character> NUMBERS_TEMP = new HashSet<Character>();
		for (char c = '0'; c <= '9'; c++) {
			NUMBERS_TEMP.add(c);
		}
		NUMBERS = Collections.unmodifiableSet(NUMBERS_TEMP);

		// initialization of the letters
		Set<Character> LETTERS_TEMP = new HashSet<Character>();
		LETTERS_TEMP.addAll(LOWER_CASE_LETTERS);
		LETTERS_TEMP.addAll(UPPER_CASE_LETTERS);
		LETTERS = Collections.unmodifiableSet(LETTERS_TEMP);

		// initialization of the alpha-numeric characters
		Set<Character> ALPHA_NUMERIC_CHARS_TEMP = new HashSet<Character>();
		ALPHA_NUMERIC_CHARS_TEMP.addAll(LETTERS);
		ALPHA_NUMERIC_CHARS_TEMP.addAll(NUMBERS);
		ALPHA_NUMERIC_CHARS = Collections.unmodifiableSet(ALPHA_NUMERIC_CHARS_TEMP);
	}

	public CharParser(boolean ignoreIllegalTransition) {
		super(ignoreIllegalTransition);
	}

	public void parse(String string) {
		Collection<Character> fsmTape = new ArrayList<Character>();
		for (char c : string.toCharArray()) {
			fsmTape.add(c);
		}
		final Pair<Boolean, S> response = process(fsmTape);
		if (!response.getFirst()) {
			throw new FiniteStateMachineException("A parser ended up in a non-accept state '" + response.getSecond() + "'. Input '" + string + "' is invalid.");
		}
	}
}