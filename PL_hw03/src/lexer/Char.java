package lexer;

class Char {
	private final char value;
	private final CharacterType type;

	enum CharacterType {
		LETTER, DIGIT, SPECIAL_CHAR, WS, END_OF_STREAM,
	}
	
	static Char of(char ch) {
		return new Char(ch, getType(ch));
	}
	
	static Char end() {
		return new Char(Character.MIN_VALUE, CharacterType.END_OF_STREAM);
	}
	
	private Char(char ch, CharacterType type) {
		this.value = ch;
		this.type = type;
	}
	
	char value() {
		return this.value;
	}
	
	CharacterType type() {
		return this.type;
	}
	
	private static CharacterType getType(char ch) {
		int code = (int)ch;
		if ( (code >= (int)'A' && code <= (int)'Z')
			|| (code >= (int)'a' && code <= (int)'z')) {
			return CharacterType.LETTER;
		}
		
		if ( Character.isDigit(ch) ) {
			return CharacterType.DIGIT;
		}
		
		switch ( ch ) { // 들어온 문자가 특수문자인지 판단해주고 맞으면 SPECIAL_CHAR타입으로 리턴
			case '-': // -가 들어온 경우
				return CharacterType.SPECIAL_CHAR;
			case '+': // +가 들어온 경우
				return CharacterType.SPECIAL_CHAR;
			case '*': // *가 들어온 경우
				return CharacterType.SPECIAL_CHAR;
			case '/': // /가 들어온 경우
				return CharacterType.SPECIAL_CHAR;
			case '<': // <가 들어온 경우
				return CharacterType.SPECIAL_CHAR;
			case '=': // =가 들어온 경우
				return CharacterType.SPECIAL_CHAR;
			case '>': // >가 들어온 경우
				return CharacterType.SPECIAL_CHAR;
			case '\'': // \'가 들어온 경우
				return CharacterType.SPECIAL_CHAR;
			case '(': // (가 들어온 경우
				return CharacterType.SPECIAL_CHAR;
			case ')': // )가 들어온 경우
				return CharacterType.SPECIAL_CHAR;
			case '#': // #추가
				return CharacterType.SPECIAL_CHAR;
			case '?': // ?추가
				return CharacterType.SPECIAL_CHAR;		
		}
		
		if ( Character.isWhitespace(ch) ) {
			return CharacterType.WS;
		}
		
		throw new IllegalArgumentException("input=" + ch);
	}
}
