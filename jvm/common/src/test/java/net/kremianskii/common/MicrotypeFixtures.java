package net.kremianskii.common;

class MicrotypeFixtures {

    static class IntegerMicrotype extends Microtype<Integer> {
        IntegerMicrotype(int value) {
            super(value);
        }
    }

    static class FloatMicrotype extends Microtype<Float> {
        FloatMicrotype(float value) {
            super(value);
        }

        FloatMicrotype(double value) {
            super((float) value);
        }
    }

    static class StringMicrotype extends Microtype<String> {
        StringMicrotype(String value) {
            super(value);
        }
    }
}
