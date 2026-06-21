package com.example.board.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.board.dto.AdminMemberDTO;

public class AdminUserDetails implements UserDetails {

	private final AdminMemberDTO adminMemberDTO;

	public AdminUserDetails(AdminMemberDTO adminMemberDTO) {
		this.adminMemberDTO = adminMemberDTO;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(
				new SimpleGrantedAuthority("ROLE_" + adminMemberDTO.getAdminRole())
		);
	}

	@Override
	public String getPassword() {
		return adminMemberDTO.getPassword();
	}

	@Override
	public String getUsername() {
		return adminMemberDTO.getAdminId();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !"BLOCKED".equals(adminMemberDTO.getAdminStatus());
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return "ACTIVE".equals(adminMemberDTO.getAdminStatus());
	}
}
