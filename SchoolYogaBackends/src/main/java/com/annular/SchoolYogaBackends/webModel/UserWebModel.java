package com.annular.SchoolYogaBackends.webModel;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserWebModel {

    private Integer userId;
    private String emailId;
    private String password;
    private String userType;
    private Boolean userIsActive;
    private String currentAddress;
    private Integer createdBy;
    private Date userCreatedOn;
    private Integer userUpdatedBy;
    private Date userUpdatedOn;
    private String userName;
	private String gender;
	private Integer schoolName;
	private String rollNo;
	private Integer std;
	private Integer age;
	private Integer profilePic;
	private Integer smilePic;
	private String token;
	private String categoryName;
	private List<HashMap<String, Object>> categoryNames;
	private String frdName;
	private String frdDescription;
	private String empId;
    private String idToken;
    private String accessToken;
	
    

}
