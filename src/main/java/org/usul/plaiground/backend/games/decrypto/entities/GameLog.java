package org.usul.plaiground.backend.games.decrypto.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GameLog extends ParentEntity {
    private List<Round> rounds = new ArrayList<>();

    public void reset() {
        this.rounds.clear();
    }

    public void addRound(Team team1, Team team2) {
        Round newRound = new Round();
        newRound.setTeam1(team1);
        newRound.setTeam2(team2);

        if (this.rounds.isEmpty()) {
            this.rounds.add(newRound);
            return;
        }

        Round prevRound = this.rounds.getLast();
        newRound.setTeam1(team1);
        newRound.setTeam2(team2);
        newRound.setTeam1MissCommTokens(prevRound.getTeam1MissCommTokens());
        newRound.setTeam2MissCommTokens(prevRound.getTeam2MissCommTokens());
        newRound.setTeam1InterceptTokens(prevRound.getTeam1InterceptTokens());
        newRound.setTeam2InterceptTokens(prevRound.getTeam2InterceptTokens());
        this.rounds.add(newRound);
    }

    public List<String> getPastCluesForCodeDigit(int codeDigit, Team team) {
        return this.getPastCluesForCodeDigit(codeDigit, team, -1);
    }

    public List<String> getPastCluesForCodeDigit(int codeDigit, Team team, int endRoundNumberExcluding) {
        List<String> pastCluesForCodeDigit = new ArrayList<>();

        for (Round round : this.getRounds()) {
            if ((endRoundNumberExcluding >= 0) && (round.getRoundNumber() == endRoundNumberExcluding)) {
                break;
            }

            TeamRound teamRound = round.getTeamInfo().get(team.getName());
            if (teamRound == null) {
                continue;
            }
            if (!teamRound.getCode().contains(codeDigit)) {
                continue;
            }
            if (teamRound.getEncryptedCode().isEmpty()) {
                continue;
            }

            int codeDigitPosition = teamRound.getCode().indexOf(codeDigit);

            String pastClue = teamRound.getEncryptedCode().get(codeDigitPosition);
            pastCluesForCodeDigit.add(pastClue);
        }

        return pastCluesForCodeDigit;
    }
}
