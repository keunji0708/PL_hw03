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
				case LETTER: //문자가 들어오면 ID상태로 간다
					context.append(v);
					return GOTO_ACCEPT_ID;
				case DIGIT: //숫자가 들어오면 INT상태로 간다.
					context.append(v);
					return GOTO_ACCEPT_INT;
				case SPECIAL_CHAR://특수문자가 들어온 경우 SIGN상태로 간다.
					if (v == '#') { //만약 #인 경우에는 SHARF_TF상태로 간다.
						context.append(v);
						return GOTO_SHARP_TF;
					}
					context.append(v);
					return GOTO_SIGN;
				case WS://빈칸이면 다시 START상태로 간다.
					return GOTO_START;
				case END_OF_STREAM://끝나면 EOS상태로 간다.
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
				case DIGIT: //문자와, 숫자가 들어오면 다시 ID상태로 간다.
					context.append(v);
					return GOTO_ACCEPT_ID;
				case SPECIAL_CHAR: //특수문자가 들어오면 FAILED상태로 간다.
					if (v == '?') { // 만약 ?인 경우에는 스트링이 키워드일수 있으므로(ex.null?일 때) 
						context.append(v);
						
						//GOTO_MATCHED를 사용하여 키워드인지 판단을 해준다.
						return GOTO_MATCHED(Token.ofName(context.getLexime()));
					}
					else
						return GOTO_FAILED;
				case WS:
				case END_OF_STREAM: //빈칸이 오거나 끝나면 스트링이 키워드인지 확인해준다.
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
				case LETTER: // 문자가 오면 FAILED상태로 간다.
					return GOTO_FAILED;
				case DIGIT: // 숫자가 오면 다시 INT상태로 간다.
					context.append(ch.value());
					return GOTO_ACCEPT_INT;
				case SPECIAL_CHAR: // 특수문자가 오면 FAILED상태로 간다.
					return GOTO_FAILED;
				case WS:
				case END_OF_STREAM: // 빈칸이 오거나 끝나면 GOTO_MATCHED를 호출,토큰타입이 INT인 토큰을 만들어준다.
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
				case LETTER: //문자가 오면 FAILED상태로 간다.
					return GOTO_FAILED;
				case DIGIT: // 숫자가 오면 INT상태로 간다.(-3처럼 앞에 특수문자가 있는 경우가 있어서)
					context.append(v);
					return GOTO_ACCEPT_INT;
				case WS:
				case END_OF_STREAM: // 빈칸이 오거나 끝나면 특수문자가 토큰타입에 있는지 확인을 하고,
					                // 있다면 토큰을 만들어 출력해준다.
					char temp = context.getLexime().charAt(0); 
					context.append(temp);
					return GOTO_MATCHED(TokenType.fromSpecialCharactor(temp), 
							context.getLexime());
				default:
					throw new AssertionError();
			}
		}
	},
	SHARP_TF{//처음에 #이 들어온 경우
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();
			switch ( ch.type() ) {
			case LETTER://뒤에 문자로 T나 F가 오면 토큰타입이 TRUE또는 FALSE인 토크을 만들어주고 출력한다.
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
