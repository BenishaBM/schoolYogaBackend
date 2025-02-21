package com.annular.SchoolYogaBackends.service;

import com.annular.SchoolYogaBackends.model.RefreshToken;
import com.annular.SchoolYogaBackends.webModel.UserWebModel;

public interface AuthenticationService {

	RefreshToken createRefreshToken(UserWebModel userWebModel);

}
