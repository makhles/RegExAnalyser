package model.regex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import model.automaton.Automaton;
import model.automaton.State;

public class RegExTree {

    private RegEx root;
    private Set<String> vocabulary;
    private RegEx lambda;

    public RegExTree(RegEx root) {
        this.root = root;
        lambda = new Lambda();
        linkNodes();
        vocabulary = new TreeSet<>();
        fillVocabulary(vocabulary);
    }

    private void fillVocabulary(Set<String> vocabulary) {
        root.fillVocabulary(vocabulary);
    }

    @Override
    public String toString() {
        return root.toString();
    }

    private void linkNodes() {
        RegEx lastNode = root.knit();
        lastNode.setThread(lambda);
    }

    public Automaton convertToDFA() {
        Map<State, Set<RegEx>> compositions = new HashMap<>();
        Queue<State> pendingStates = new LinkedList<>();
        Set<RegEx> currentComposition = root.moveDown();
        Automaton dfa = new Automaton(new ArrayList<>(vocabulary));
        State currentState = new State(dfa.nextLabel());

        dfa.setInitialState(currentState);
        pendingStates.add(currentState);
        compositions.put(currentState, currentComposition);

        while (!pendingStates.isEmpty()) {

            List<State> toStates = new ArrayList<>();
            currentState = pendingStates.poll();
            currentComposition = compositions.get(currentState);

            if (currentComposition.contains(lambda)) {
                dfa.addAcceptingState(currentState);
            }

            for (String symbol : vocabulary) {
                Set<RegEx> symbolNodes = new HashSet<>();
                for (RegEx node : currentComposition) {
                    if (node.data.equals(symbol)) {
                        symbolNodes.add(node);
                    }
                }
                if (!symbolNodes.isEmpty()) {
                    Set<RegEx> newComposition = new HashSet<>();
                    for (RegEx node : symbolNodes) {
                        newComposition.addAll(node.moveUp());
                    }
                    boolean shouldCreateNewState = true;
                    for (Map.Entry<State, Set<RegEx>> composition : compositions.entrySet()) {
                        if (newComposition.equals(composition.getValue())) {
                            toStates.add(composition.getKey());
                            shouldCreateNewState = false;
                            break;
                        }
                    }
                    if (shouldCreateNewState) {
                        State state = new State(dfa.nextLabel());
                        toStates.add(state);
                        compositions.put(state, newComposition);
                        pendingStates.add(state);
                    }
                } else {
                    toStates.add(State.ERROR_STATE);
                }
            }
            dfa.addTransitions(currentState, toStates);
        }
        return dfa;
    }
}
