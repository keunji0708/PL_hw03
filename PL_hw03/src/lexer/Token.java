package lexer;


import java.util.HashMap;
import java.util.Map;

public class Token {
	private final TokenType type;
	private final String lexme;
	
	static Token ofName(String lexme) {
		TokenType type = KEYWORDS.get(lexme);
		if (type != null) {//KEYWORDSŸ�� ��ū ����
			return new Token(type, lexme);
		}
		else if (lexme.endsWith("?")) { // �ڿ� ?�� ������ Ű������ ���
			if (lexme.substring(0, lexme.length()-1).contains("?")) {
				throw new ScannerException("invalid ID=" + lexme);
			}
			
			return new Token(TokenType.QUESTION, lexme);
		}
		else if (lexme.contains("?")) { // �������� �ƴѰ��� ?�� ���� ��� ����ó��
			throw new ScannerException("invalid ID=" + lexme);
		}
		else { // Ű���尡 �ƴϹǷ� IDŸ������ ��ū����
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
	static { // �ش� Ű���� ��Ʈ���� ������ ������ ��ūŸ���� �����ش�.
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
