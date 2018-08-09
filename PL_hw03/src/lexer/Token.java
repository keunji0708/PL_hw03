package lexer;


import java.util.HashMap;
import java.util.Map;

public class Token {
	private final TokenType type;
	private final String lexme;
	
	static Token ofName(String lexme) {
		TokenType type = KEYWORDS.get(lexme);
		if (type != null) {//KEYWORDS타입 토큰 생성
			return new Token(type, lexme);
		}
		else if (lexme.endsWith("?")) { // 뒤에 ?로 끝나는 키워드인 경우
			if (lexme.substring(0, lexme.length()-1).contains("?")) {
				throw new ScannerException("invalid ID=" + lexme);
			}
			
			return new Token(TokenType.QUESTION, lexme);
		}
		else if (lexme.contains("?")) { // 마지막이 아닌곳에 ?가 들어온 경우 예외처리
			throw new ScannerException("invalid ID=" + lexme);
		}
		else { // 키워드가 아니므로 ID타입으로 토큰생성
			return new Token(TokenType.ID, lexme);
		}
	}
	
	Token(TokenType type, String lexme) {
		this.type = type;
		this.lexme = lexme;
	}
	
	public TokenType type() {
		return this.type;
	}
	
	public String lexme() {
		return this.lexme;
	}
	
	@Override
	public String toString() {
		return String.format("%s(%s)", type, lexme);
	}
	
	private static final Map<String,TokenType> KEYWORDS = new HashMap<>();
	static { // 해당 키워드 스트링이 들어오면 각각의 토큰타입을 정해준다.
		KEYWORDS.put("define", TokenType.DEFINE);
		KEYWORDS.put("lambda", TokenType.LAMBDA);
		KEYWORDS.put("cond", TokenType.COND);
		KEYWORDS.put("quote", TokenType.QUOTE);
		KEYWORDS.put("not", TokenType.NOT);
		KEYWORDS.put("cdr", TokenType.CDR);
		KEYWORDS.put("car", TokenType.CAR);
		KEYWORDS.put("cons", TokenType.CONS);
		KEYWORDS.put("eq?", TokenType.EQ_Q);
		KEYWORDS.put("null?", TokenType.NULL_Q);
		KEYWORDS.put("atom?", TokenType.ATOM_Q);
	}
}
