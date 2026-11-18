package com.Tender.status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
	
	private String status;
	private String message;
	private T payload;

}
