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

package br.unisinos.evertonlucas.passshelter.rep;

import android.database.sqlite.SQLiteConstraintException;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import br.unisinos.evertonlucas.passshelter.encryption.SymmetricEncryption;
import br.unisinos.evertonlucas.passshelter.model.Resource;
import br.unisinos.evertonlucas.passshelter.util.KeyGenerationUtil;

/**
 * Class created to test ResourceRep
 * Created by everton on 31/08/15.
 */
public class ResourceRepTest extends AndroidTestCase {

    private static final String TEST_PREFIX = "test_";
    private SymmetricEncryption encryption;
    private ResourceRep resourceRep;
    private String resName = "Facebook";
    private String user = "User";
    private String password = "123456";

    public void setUp() throws Exception {
        super.setUp();

        final SecretKey key = KeyGenerationUtil.generate();
        this.encryption = new SymmetricEncryption(key);

        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), TEST_PREFIX);
        resourceRep = new ResourceRep(context, encryption);
        insertResource(this.resName, this.user, this.password);
    }

    public void tearDown() throws Exception {
        super.tearDown();

        resourceRep = null;
    }

    public void testInsertResource() throws Exception {
        String resource = "resource";
        String usr = "usr";
        String pwd = "pwd";
        insertResource(resource, usr, pwd);
        Resource resQuery = resourceRep.getResourceByName(resource);
        assertNotNull(resQuery);
        assertNotNull(resQuery.getId());
        assertEquals(resource, resQuery.getName());
        assertEquals(usr, resQuery.getUser());
        assertEquals(pwd, resQuery.getPassword());
    }

    private void insertResource(String resName, String user, String password) throws Exception {
        Resource resource = getResource(resName, user, password);
        resourceRep.saveResource(resource);
    }

    public void testAvoidDuplicateInsertion() throws Exception {
        try {
            insertResource(resName, this.user, password);
            fail("Could not reach here");
        } catch (SQLiteConstraintException e) {
            assertTrue(true);
        }
    }

    public void testUpdateResource() throws Exception {
        String name = "face";
        String user2 = "user2";
        String password = "zyxcba";
        Resource resQuery = resourceRep.getResourceByName(this.resName);
        resQuery.setName(name);
        resQuery.setUser(user2);
        resQuery.setPassword(password);
        resourceRep.saveResource(resQuery);
        Resource resQuery2 = resourceRep.getResourceByName(name);
        assertNotNull(resQuery2);
        assertTrue(resQuery2.getName().equals(name));
        assertTrue(resQuery2.getUser().equals(user2));
        assertTrue(resQuery2.getPassword().equals(password));
    }

    public void testAvoidDuplicateUpdate() throws Exception {
        try {
            String name = "face";
            String user2 = "user2";
            String password = "zyxcba";
            insertResource(name, user2, password);
            Resource resQuery = resourceRep.getResourceByName(name);
            resQuery.setName(this.resName);
            resourceRep.updateResource(resQuery);
        } catch (SQLiteConstraintException e) {
            assertTrue(true);
        }
    }

    private Resource getResource(String name, String user, String password) {
        Resource resource = new Resource(encryption);
        resource.setName(name);
        resource.setUser(user);
        resource.setPassword(password);
        return resource;
    }

    public void testGetAllResources() throws IllegalBlockSizeException, NoSuchPaddingException,
            BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        List<Resource> list = resourceRep.getAllResources();
        assertTrue(list.size() > 0);
    }

    public void testDeleteResource() throws IllegalBlockSizeException, NoSuchPaddingException,
            BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Resource resource = resourceRep.getResourceByName(this.resName);
        this.resourceRep.delete(resource);
        Resource resQuery = resourceRep.getResourceByName(this.resName);
        assertNull(resQuery);
    }
}