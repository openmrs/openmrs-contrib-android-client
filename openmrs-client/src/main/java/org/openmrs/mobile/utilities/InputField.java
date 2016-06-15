package org.openmrs.mobile.utilities;


import java.util.concurrent.atomic.AtomicInteger;

    public class InputField {
        int id;
        String concept;
        double value;

        public void setId(int id)
        {
            this.id=id;
        }

        public int getId()
        {
            return id;
        }

        public void setConcept(String concept)
        {
            this.concept=concept;
        }

        public String getConcept()
        {
            return concept;
        }

        public void setValue(Double value)
        {
            this.value=value;
        }

        public Double getValue()
        {
            return value;
        }

        private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

        public static int generateViewId() {
            while (true) {
                final int result = sNextGeneratedId.get();
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF) newValue = 1;
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        }

    }
