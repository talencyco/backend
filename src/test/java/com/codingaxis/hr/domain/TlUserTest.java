package com.codingaxis.hr.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingaxis.hr.TestUtil;
import org.junit.jupiter.api.Test;


public class TlUserTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TlUser.class);
        TlUser tlUser1 = new TlUser();
        tlUser1.id = 1L;
        TlUser tlUser2 = new TlUser();
        tlUser2.id = tlUser1.id;
        assertThat(tlUser1).isEqualTo(tlUser2);
        tlUser2.id = 2L;
        assertThat(tlUser1).isNotEqualTo(tlUser2);
        tlUser1.id = null;
        assertThat(tlUser1).isNotEqualTo(tlUser2);
    }
}
