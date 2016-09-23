package com.capgemini.chess.service;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BoardManagerTest.class, ChallengeServiceTest.class, GameServiceTest.class, 
	RankServiceTest.class, StatisticsServiceTest.class, UserServiceTest.class })
public class ServiceTestSuite {

}
