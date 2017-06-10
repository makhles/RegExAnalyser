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

    @Override
    public String toString() {
        return root.toString();
    }

    private void linkNodes() {
        RegEx lastNode = root.knit();
        lastNode.setThread(lambda);
    }

    public Automaton convertToDFA() {
        Automaton dfa = new Automaton(new ArrayList<>(vocabulary));
        Queue<String> pendingStates = new LinkedList<>();
        Map<String, Set<RegEx>> compositions = new HashMap<>();
        Set<RegEx> composition = root.moveDown();
        String currentState = dfa.nextLabel();

        dfa.setInitialState(currentState);
        pendingStates.add(currentState);
        compositions.put(currentState, composition);

        while (!pendingStates.isEmpty()) {

            List<Set<String>> toStates = new ArrayList<>();
            currentState = pendingStates.poll();
            composition = compositions.get(currentState);

            if (composition.contains(lambda)) {
                dfa.addAcceptingState(currentState);
            }

            Set<String> stateSet = new HashSet<>();
            for (String symbol : vocabulary) {
                Set<RegEx> symbolNodes = new HashSet<>();
                for (RegEx node : composition) {
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
                    for (Map.Entry<String, Set<RegEx>> entryComposition : compositions.entrySet()) {
                        if (newComposition.equals(entryComposition.getValue())) {
                            stateSet.add(entryComposition.getKey());
                            toStates.add(stateSet);
                            shouldCreateNewState = false;
                            break;
                        }
                    }
                    if (shouldCreateNewState) {
                        String state = dfa.nextLabel();
                        stateSet.add(state);
                        toStates.add(stateSet);
                        compositions.put(state, newComposition);
                        pendingStates.add(state);
                    }
                } else {
                    stateSet.add(Automaton.ERROR_STATE);
                    toStates.add(stateSet);
                }
            }
            dfa.addTransitions(currentState, toStates);
        }
        return dfa;
    }

    private void fillVocabulary(Set<String> vocabulary) {
        root.fillVocabulary(vocabulary);
    }
}
