package com.annular.SchoolYogaBackends.webModel;

import java.util.Date;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryWebModel {
	

	private Integer categoryId;
	private String categoryName;
	private Boolean categoryIsActive;
	private Integer categorycreatedBy;
	private Date categoryCreatedOn;
	private Integer category;
	private Date categoryUpdatedOn;

}
