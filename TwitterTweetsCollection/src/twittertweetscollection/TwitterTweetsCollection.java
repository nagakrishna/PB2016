/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//http://stackoverflow.com/questions/18016532/stop-the-twitter-stream-and-return-list-of-status-with-twitter4j
package twittertweetscollection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.*;
/**
 *
 * @author Naga Krishna
 */

    public class TwitterTweetsCollection {

  public static void main(String[] args) throws TwitterException, IOException {
      StringBuilder stringBuilder = new StringBuilder();
      TwitterTweetsCollection stream = new TwitterTweetsCollection();
      List<Status> n = stream.execute();
      for (Status s : n)
      {
           stringBuilder.append(s);
       }

 
    	   File output = new File("E:/PB/second.txt");
//           if(!output.exists())
//           {
//           	output.createNewFile();
//           }
           FileWriter fw = new FileWriter(output.getAbsolutePath());
            BufferedWriter tweetWriter = new BufferedWriter(fw);
            tweetWriter.write(stringBuilder.toString());
            tweetWriter.close();
  }

  private final Object lock = new Object();
  public List<Status> execute() throws TwitterException {

    final List<Status> statuses = new ArrayList();

    ConfigurationBuilder cb = new ConfigurationBuilder();
    cb.setDebugEnabled(true)
                .setOAuthConsumerKey("I7WYWBE6eSNKdV4FYSyiuP0uk")
                .setOAuthConsumerSecret("oft7Y6WcUCvxovPU5modCZ3BOlHH2z9Px73UkFMILN4ltBhOni")
                .setOAuthAccessToken("3524247253-o7zXFn3r8PwPb4IUTYqHRQzVabTzfoFn9Ck7FzW")
                .setOAuthAccessTokenSecret("V7zkLiJmF0vsdX8rUdkXo3q1ha452vKqVtB5OoCuQWAgR");

    TwitterStream twitterStream = new TwitterStreamFactory(cb.build())
        .getInstance();

    StatusListener listener = new StatusListener() {

      public void onStatus(Status status) {
        statuses.add(status);
        System.out.println(statuses.size() + ":" + status.getText());
        if (statuses.size() > 100) {
          synchronized (lock) {
            lock.notify();
          }
          System.out.println("unlocked");
        }
      }

      public void onDeletionNotice(
          StatusDeletionNotice statusDeletionNotice) {
        System.out.println("Got a status deletion notice id:"
            + statusDeletionNotice.getStatusId());
      }

      public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        System.out.println("Got track limitation notice:"
            + numberOfLimitedStatuses);
      }

      public void onScrubGeo(long userId, long upToStatusId) {
        System.out.println("Got scrub_geo event userId:" + userId
            + " upToStatusId:" + upToStatusId);
      }

      public void onException(Exception ex) {
        ex.printStackTrace();
      }

      @Override
      public void onStallWarning(StallWarning sw) {
        System.out.println(sw.getMessage());

      }
    };

    //FilterQuery fq = new FilterQuery();
    //String keywords[] = { "SuperBowlSunday"};

    //fq.track(keywords);


    twitterStream.addListener(listener);
    twitterStream.sample();
    //twitterStream.filter(fq);

    try {
      synchronized (lock) {
        lock.wait();
      }
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println("returning statuses");
    twitterStream.shutdown();
    return statuses;
  }
}
    



