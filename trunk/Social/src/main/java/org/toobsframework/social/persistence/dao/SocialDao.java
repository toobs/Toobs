/*
 * This file is licensed to the Toobs Framework Group under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Toobs Framework Group licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file 
 * except in compliance with the License.  You may obtain a copy of the 
 * License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.toobsframework.social.persistence.dao;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.toobsframework.social.persistence.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Simple, but VERY unneficient dao, based on castor
 * @author jaimegarza
 *
 */
public class SocialDao {
  
  private static final Log log = LogFactory.getLog(SocialDao.class);
  
  private String dataFile = "";
  private Directory directory = null;

  /**
   * Initialize the Dao from the XML file
   */
  public void init() {
    log.info("USER.DIR="+System.getProperty("user.dir"));
    try {
      Reader reader = new FileReader(dataFile);
      directory = (Directory) Unmarshaller.unmarshal(Directory.class, reader);
    } catch (Exception e) {
      throw new RuntimeException("Error in initialization of SocialDao: " + e.getMessage(), e);
    }
  }
  
  /**
   * Save the XML file again
   */
  public void save() {
    try {
      Writer writer = new FileWriter(dataFile);
      Marshaller.marshal(directory, writer);
    } catch (Exception e) {
      throw new RuntimeException("Error in ssaving of SocialDao: " + e.getMessage(), e);
    }
  }
  
  /**
   * Locate a user with the given id
   */
  public User getUser(String userId) {
    User[] users = directory.getUser();
    for (User user : users) {
      if (user.getUserId().equals(userId)) {
        return user;
      }
    }
    return null;
  }

  /**
   * Add a user with id and name
   * @return the new user
   */
  public User addUser(String userId, String firstName, String lastName, String password) {
    User user = getUser(userId);
    if(user != null) {
      throw new RuntimeException("User " + userId + " already exists");
    }
    
    user = new User();
    user.setUserId(userId);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setPassword(password);
    directory.addUser(user);
    save();
    return user;
  }
  
  /**
   * Adds a friend to a user
   */
  public void addFriendForUser(String userId, String friendId) {
    User user = getUser(userId);
    if(user == null) {
      throw new RuntimeException("User " + userId + " does not exists");
    }
    
    Friend friend = new Friend();
    friend.setUserId(friendId);
    user.addFriend(friend);
    save();
  }

  /**
   * @return all the posts for a user
   */
  public List<Post> getPostsForAUser(String userId) {
    List<Post> posts = new ArrayList<Post>();
    Post[] allPosts = directory.getPost();
    for (Post post : allPosts) {
      if (post.getTo().equals(userId)) {
        posts.add(post);
      }
    }
    return posts;
  }

  /**
   * @return all the posts for all user's friends
   */
  public List<Post> getPostsForAUserFriends(String userId) {
    List<Post> posts = new ArrayList<Post>();
    User user = getUser(userId);
    
    if (user == null) {
      return posts;
    }
    
    Friend[] friends = user.getFriend();
    
    if (friends == null) {
      return posts;
    }
    
    Post[] allPosts = directory.getPost();
    for (Post post : allPosts) {
      for (Friend friend : friends) {
        if (post.getFrom().equals(friend.getUserId()) ||
            post.getFrom().equals(userId) ||
            post.getTo().equals(userId)) {
          posts.add(post);
          break;
        }
      }
    }
    return posts;
  }
  
  /**
   * add a post to the list
   */
  
  public void addPost(String fromUserId, String toUserId, String comment) {
    Post post = new Post();
    post.setFrom(fromUserId);
    post.setTo(toUserId);
    post.setComment(comment);
    post.setOn(new Date());
    directory.addPost(post);
    save();
  }

  /**
   * @return the dataFile
   */
  public String getDataFile() {
    return dataFile;
  }

  /**
   * @param dataFile the dataFile to set
   */
  public void setDataFile(String dataFile) {
    this.dataFile = dataFile;
  }

}
