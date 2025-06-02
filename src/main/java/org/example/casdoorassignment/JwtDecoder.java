package org.example.casdoorassignment;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.SignedJWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

@Service
public class JwtDecoder {
    private final CasdoorProperties openIdConnectProperties;
    private final RestTemplate restTemplate; 

    @Autowired // Constructor injection
    public JwtDecoder(CasdoorProperties openIdConnectProperties, RestTemplate restTemplate) {
        this.openIdConnectProperties = openIdConnectProperties;
        this.restTemplate = restTemplate;
    }

    public Claims decodeToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSHeader header = signedJWT.getHeader();
            String kid = header.getKeyID();

            if (kid == null) {
                throw new RuntimeException("Token does not contain 'kid' (Key ID) in header");
            }

            String jwksUri = openIdConnectProperties.getEndpoint() + "/.well-known/jwks"; 
            System.out.println("Fetching JWKS from: " + jwksUri);

            String jwksResponse = restTemplate.getForObject(jwksUri, String.class);
            if (jwksResponse == null) {
                throw new RuntimeException("Failed to fetch JWKS content from " + jwksUri);
            }
            JWKSet jwkSet = JWKSet.parse(jwksResponse);

            JWK jwk = jwkSet.getKeyByKeyId(kid);

            if (jwk == null) {
                throw new RuntimeException("No matching key found in JWKSet for kid: " + kid
                        + ". Available KIDs in JWKS: " + jwkSet.getKeys().stream().map(JWK::getKeyID).toList());
            }

            if (!(jwk.toRSAKey() != null)) {
                throw new RuntimeException("JWK is not an RSA key. Kid: " + kid);
            }
            RSAPublicKey publicKey = (RSAPublicKey) jwk.toRSAKey().toPublicKey();


            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ParseException e) {
            System.err.println("Failed to parse JWT: " + e.getMessage());
            throw new RuntimeException("Failed to parse JWT", e);
        } catch (Exception e) {
            System.err.println("Failed to decode token (Full Trace):");
            e.printStackTrace();
            throw new RuntimeException("Failed to decode token", e);
        }
    }
}