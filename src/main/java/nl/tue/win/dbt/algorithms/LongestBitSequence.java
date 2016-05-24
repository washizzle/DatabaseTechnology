package nl.tue.win.dbt.algorithms;

import java.util.BitSet;
import java.util.function.BinaryOperator;

public interface LongestBitSequence {
    int longestSetBits(final BitSet bs);
    int longestUnsetBits(final BitSet bs);

    // https://stackoverflow.com/questions/35244340/longest-sequence-of-numbers
    class LinearLongestBitSequence implements LongestBitSequence {

        @Override
        public int longestSetBits(final BitSet bs) {
            return longestBits(bs, (setIndex, unsetIndex) -> unsetIndex - setIndex);
        }

        @Override
        public int longestUnsetBits(final BitSet bs) {
            return longestBits(bs, (setIndex, unsetIndex) -> setIndex - unsetIndex);
        }

        private int longestBits(final BitSet bs, final BinaryOperator<Integer> difference) {
            int maxLength = 0;
            int index = 0;
            int setIndex;
            int unsetIndex;
            int length;
            do {
                setIndex = bs.nextSetBit(index);
                unsetIndex = bs.nextClearBit(index);
                length = difference.apply(setIndex, unsetIndex);
                if(length > maxLength) {
                    maxLength = length;
                }
                index = Math.max(setIndex, unsetIndex);
            } while(index >= 0);
            return maxLength;
        }
    }

    class SimpleLongestBitSequence implements LongestBitSequence {

        @Override
        public int longestSetBits(BitSet bs) {
            return count(bs, true);
        }

        @Override
        public int longestUnsetBits(BitSet bs) {
            return count(bs, false);
        }

        private int count(BitSet bs, boolean state) {
            int max = 0;
            int length = 0;
            for (int i = 0; i < bs.length(); i++) {
                if(bs.get(i) == state) {
                    ++length;
                } else {
                    max = length;
                    length = 0;
                }
            }
            return max;
        }
    }
}
