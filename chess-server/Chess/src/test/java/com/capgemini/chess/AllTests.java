package com.capgemini.chess;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.capgemini.chess.batch.BatchTestSuite;
import com.capgemini.chess.dataaccess.DataaccessTestSuite;
import com.capgemini.chess.rest.RestTestSuite;
import com.capgemini.chess.service.ServiceTestSuite;

@RunWith(Suite.class)
@SuiteClasses({BatchTestSuite.class, DataaccessTestSuite.class, RestTestSuite.class, ServiceTestSuite.class})
public class AllTests {

}
