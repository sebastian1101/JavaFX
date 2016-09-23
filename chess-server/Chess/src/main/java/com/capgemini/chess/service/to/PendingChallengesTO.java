package com.capgemini.chess.service.to;

import java.util.ArrayList;
import java.util.List;

import com.capgemini.chess.dataaccess.entities.ChallengeEntity;

/**
 * Transport object which represents lists of pending {@link ChallengeEntity}.
 * 
 * @author Michal Bejm
 *
 */
public class PendingChallengesTO {
	
	private List<ChallengeLineTO> sentChallenges = new ArrayList<>();
	private List<ChallengeLineTO> receivedChallenges = new ArrayList<>();

	public List<ChallengeLineTO> getSentChallenges() {
		return sentChallenges;
	}

	public List<ChallengeLineTO> getReceivedChallenges() {
		return receivedChallenges;
	}
	
}
