package com.example.timesheetapp.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.timesheetapp.entities.Utilisateur;
import com.example.timesheetapp.services.AccountService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class JWTAuthorizationFiler extends OncePerRequestFilter {

    AccountService accountService ;

    public JWTAuthorizationFiler(AccountService accountService){
     this.accountService = accountService ;
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin","*");
        response.addHeader("Access-Control-Allow-Methods","*");
        //response.addHeader("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers,authorization");
        response.addHeader("Access-Control-Allow-Headers","*");
        response.addHeader("Access-Control-Expose-Headers","Access-Control-Allow-Origin, Access-Control-Allow-Credentials, authorization");
        if(request.getMethod().equals("OPTIONS")){
            response.setStatus(HttpServletResponse.SC_OK);
        }
        else if(request.getRequestURI().equals("/login")) {
            filterChain.doFilter(request, response);     //filterChain.doFilter(request, response);
            return;
        }
        else {
            String jwtToken = request.getHeader(SecurityParams.JWT_HEADER_NAME);

            if (jwtToken == null || !jwtToken.startsWith(SecurityParams.HEADER_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SecurityParams.SECRET)).build();
            String jwt = jwtToken.substring(SecurityParams.HEADER_PREFIX.length());
            DecodedJWT decodedJWT = verifier.verify(jwt);

            String username = decodedJWT.getSubject();

            Utilisateur utilisateur = accountService.loadUserByUsername(username).orElse(new Utilisateur());
            if (utilisateur.getEnabled() == false ) {
                // System.out.println("blocked");
                // throw new ServletException("Invalid token" + "");
                // response.setStatus(HttpServletResponse.SC_FORBIDDEN);
              
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is not valid.");

            } else {


                List<String> roles = decodedJWT.getClaims().get("roles").asList(String.class);
                Collection<GrantedAuthority> authorities = new ArrayList<>();

                roles.forEach(rn -> {
                    authorities.add(new SimpleGrantedAuthority(rn));
                });

                UsernamePasswordAuthenticationToken user =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(user);
                filterChain.doFilter(request, response);
            }

        }

    }
}
