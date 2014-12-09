package jp.co.jbat.qanat.rest.entity;


public class QanatErrorResponse {
	private QanatError error;
	
	public QanatErrorResponse() {
		error = new QanatError();
	}

	public final QanatError getError() {
		return error;
	}

	public final void setError(QanatError error) {
		this.error = error;
	}

}
