/*
Copyright 2015 Everton Luiz de Resende Lucas

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package br.unisinos.evertonlucas.passshelter.model;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import br.unisinos.evertonlucas.passshelter.encryption.KeyFactory;

/**
 * Class which represents an user
 * Created by everton on 27/09/15.
 */
public class User {

    private Long id;
    private String email;
    private PublicKey publicKey;
    private String remoteId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public User copy() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        User user = new User();
        user.setId(this.id);
        user.setEmail(this.email);
        user.setRemoteId(this.remoteId);
        user.setPublicKey(KeyFactory.generatePublicKey(this.publicKey.getEncoded()));
        return user;
    }
}
