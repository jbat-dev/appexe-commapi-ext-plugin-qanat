package jp.co.jbat.qanat.exception;

/**
 * Rest API の例外クラス
 * 
 * @version 1.0
 * @author J34490
 */
public class QanatNetworkException extends Exception {
	/** serialVersionUID */
	private static final long serialVersionUID = 355572527841327452L;

	/** error code */
	private Integer code = null;

	/**
	 * コンストラクタ
	 * 
	 * @param s 例外メッセージ
	 */
	public QanatNetworkException(String s) {
		super(s);
	}
	/**
	 * コンストラクタ
	 * 
	 * @param c 例外コード
	 * @param s 例外メッセージ
	 */
	public QanatNetworkException(int c, String s) {
		super(s);
		this.code = Integer.valueOf(c) ;
	}
	/**
	 * コンストラクタ
	 * 
	 * @param t 例外オブジェクト
	 */
	public QanatNetworkException(Throwable t) {
		super(t);
	}
	/**
	 * コンストラクタ
	 * 
	 * @param s 例外メッセージ
	 * @param t 例外オブジェクト
	 */
	public QanatNetworkException(String s, Throwable t) {
		super(s, t);
	}
	/**
	 * コンストラクタ
	 * 
	 * @param c 例外コード
	 * @param s 例外メッセージ
	 * @param t 例外オブジェクト
	 */
	public QanatNetworkException(int c, String s, Throwable t) {
		super(s, t);
		this.code = Integer.valueOf(c) ;
	}

	/**
	 * 例外コードを取得する
	 * 
	 * @return 例外コード
	 */
	public Integer getCode() {
		return this.code;
	}

}
