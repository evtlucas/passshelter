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

package br.unisinos.evertonlucas.passshelter.encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

/**
 * Class designed for assymetric decryption
 * Created by everton on 20/09/15.
 */
public class AssymetricDecryption {

    private final Cipher decCipher;

    public AssymetricDecryption(Cipher encCipher) {
        this.decCipher = encCipher;
    }

    public byte[] decrypt(byte[] bytes) throws BadPaddingException, IllegalBlockSizeException {
        return this.decCipher.doFinal(bytes);
    }
}
