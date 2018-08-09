package lexer;

import static lexer.TokenType.ID;
import static lexer.TokenType.INT;
import static lexer.TransitionOutput.GOTO_ACCEPT_ID;
import static lexer.TransitionOutput.GOTO_ACCEPT_INT;
import static lexer.TransitionOutput.GOTO_EOS;
import static lexer.TransitionOutput.GOTO_FAILED;
import static lexer.TransitionOutput.GOTO_MATCHED;
import static lexer.TransitionOutput.GOTO_SIGN;
import static lexer.TransitionOutput.GOTO_START;
import static lexer.TransitionOutput.GOTO_SHARP_TF;

enum State {
	START {
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();
			switch ( ch.type() ) {
				case LETTER: //���ڰ� ������ ID���·� ����
					context.append(v);
					return GOTO_ACCEPT_ID;
				case DIGIT: //���ڰ� ������ INT���·� ����.
					context.append(v);
					return GOTO_ACCEPT_INT;
				case SPECIAL_CHAR://Ư�����ڰ� ���� ��� SIGN���·� ����.
					if (v == '#') { //���� #�� ��쿡�� SHARF_TF���·� ����.
						context.append(v);
						return GOTO_SHARP_TF;
					}
					context.append(v);
					return GOTO_SIGN;
				case WS://��ĭ�̸� �ٽ� START���·� ����.
					return GOTO_START;
				case END_OF_STREAM://������ EOS���·� ����.
					return GOTO_EOS;
				default:
					throw new AssertionError();
			}
		}
	},
	ACCEPT_ID {
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();
			switch ( ch.type() ) {
				case LETTER:
				case DIGIT: //���ڿ�, ���ڰ� ������ �ٽ� ID���·� ����.
					context.append(v);
					return GOTO_ACCEPT_ID;
				case SPECIAL_CHAR: //Ư�����ڰ� ������ FAILED���·� ����.
					if (v == '?') { // ���� ?�� ��쿡�� ��Ʈ���� Ű�����ϼ� �����Ƿ�(ex.null?�� ��) 
						context.append(v);
						
						//GOTO_MATCHED�� ����Ͽ� Ű�������� �Ǵ��� ���ش�.
						return GOTO_MATCHED(Token.ofName(context.getLexime()));
					}
					else
						return GOTO_FAILED;
				case WS:
				case END_OF_STREAM: //��ĭ�� ���ų� ������ ��Ʈ���� Ű�������� Ȯ�����ش�.
					return GOTO_MATCHED(Token.ofName(context.getLexime()));
				default:
					throw new AssertionError();
			}
		}
	},
	ACCEPT_INT {
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			switch ( ch.type() ) {
				case LETTER: // ���ڰ� ���� FAILED���·� ����.
					return GOTO_FAILED;
				case DIGIT: // ���ڰ� ���� �ٽ� INT���·� ����.
					context.append(ch.value());
					return GOTO_ACCEPT_INT;
				case SPECIAL_CHAR: // Ư�����ڰ� ���� FAILED���·� ����.
					return GOTO_FAILED;
				case WS:
				case END_OF_STREAM: // ��ĭ�� ���ų� ������ GOTO_MATCHED�� ȣ��,��ūŸ���� INT�� ��ū�� ������ش�.
					return GOTO_MATCHED(INT, context.getLexime());
				default:
					throw new AssertionError();
			}
		}
	},
	SIGN {
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();
			switch ( ch.type() ) {
				case LETTER: //���ڰ� ���� FAILED���·� ����.
					return GOTO_FAILED;
				case DIGIT: // ���ڰ� ���� INT���·� ����.(-3ó�� �տ� Ư�����ڰ� �ִ� ��찡 �־)
					context.append(v);
					return GOTO_ACCEPT_INT;
				case WS:
				case END_OF_STREAM: // ��ĭ�� ���ų� ������ Ư�����ڰ� ��ūŸ�Կ� �ִ��� Ȯ���� �ϰ�,
					                // �ִٸ� ��ū�� ����� ������ش�.
					char temp = context.getLexime().charAt(0); 
					context.append(temp);
					return GOTO_MATCHED(TokenType.fromSpecialCharactor(temp), 
							context.getLexime());
				default:
					throw new AssertionError();
			}
		}
	},
	SHARP_TF{//ó���� #�� ���� ���
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();
			switch ( ch.type() ) {
			case LETTER://�ڿ� ���ڷ� T�� F�� ���� ��ūŸ���� TRUE�Ǵ� FALSE�� ��ũ�� ������ְ� ����Ѵ�.
				if (v == 'T' || v == 'F') {
					context.append(v);
					return GOTO_MATCHED(TokenType.fromSpecialCharactor(v), 
							context.getLexime());
				}
			default:
				throw new AssertionError();
				}
			}
		},
	MATCHED {
		@Override
		public TransitionOutput transit(ScanContext context) {
			throw new IllegalStateException("at final state");
		}
	},
	FAILED{
		@Override
		public TransitionOutput transit(ScanContext context) {
			throw new IllegalStateException("at final state");
		}
	},
	EOS {
		@Override
		public TransitionOutput transit(ScanContext context) {
			return GOTO_EOS;
		}
	};
	
	abstract TransitionOutput transit(ScanContext context);
}
