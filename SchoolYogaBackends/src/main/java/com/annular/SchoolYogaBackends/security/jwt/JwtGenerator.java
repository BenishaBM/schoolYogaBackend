package com.annular.SchoolYogaBackends.security.jwt;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtGenerator {

    private static final String GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
//    private static final String SECRET_KEY = "Your_Secret_Key"; // Replace with a strong key
    @Value("${annular.app.jwtExpirationMs}")
	private int jwtExpirationMs;
    
    public String generateJwt(String accessToken) throws Exception {
        // Step 1: Validate Access Token with Google and get user info
        URL url = new URL(GOOGLE_USERINFO_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);

        if (connection.getResponseCode() != 200) {
            throw new Exception("Invalid Access Token");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        // Parse user info
        JsonObject userInfo = JsonParser.parseString(response.toString()).getAsJsonObject();
        String userId = userInfo.get("sub").getAsString(); // Google user ID
        String email = userInfo.get("email").getAsString();

        // Step 2: Create JWT
        Claims claims = Jwts.claims();
		claims.put("userName", email);
		claims.put("googleId", userId);
//		claims.put("userType", userPrincipal.getUserType());
System.out.println("\n\n generation in progressdstw\n");
		byte[] keyBytes = new byte[64];
		SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA512");

		return Jwts.builder().setSubject(email).setClaims(claims).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, key).compact();
    }
}
