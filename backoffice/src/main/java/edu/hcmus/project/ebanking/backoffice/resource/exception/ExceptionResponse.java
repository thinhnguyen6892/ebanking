package edu.hcmus.project.ebanking.backoffice.resource.exception;

import java.util.Date;

public class ExceptionResponse {
	private Date timestamp;
	private String message;
	private String details;


	public ExceptionResponse(Date timestamp, String message, String details, boolean showDetails) {
		super();
		this.timestamp = timestamp;
		this.message = message;
		this.details = showDetails ? details : "";
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getMessage() {
		return message;
	}

	public String getDetails() {
		return details;
	}

}
