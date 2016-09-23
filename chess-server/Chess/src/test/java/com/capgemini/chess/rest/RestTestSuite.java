package com.capgemini.chess.rest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ChallengeRestControllerTest.class, GameRestControllerTest.class, StatisticsRestControllerTest.class,
		UserRestControllerTest.class })
public class RestTestSuite {

}
