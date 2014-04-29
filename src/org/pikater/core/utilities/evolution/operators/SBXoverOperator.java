package org.pikater.core.utilities.evolution.operators;

import org.pikater.core.utilities.evolution.Population;
import org.pikater.core.utilities.evolution.RandomNumberGenerator;
import org.pikater.core.utilities.evolution.individuals.Individual;
import org.pikater.core.utilities.evolution.individuals.RealIndividual;

public class SBXoverOperator implements Operator {

	private double xover_rate;
	private final double EPS = 0.00001;
	private final double ETA_C = 20;


	public SBXoverOperator(double cross_rate) {
		xover_rate = cross_rate;
	}

	public void operate(Population parents, Population offspring) {
		int popSize = parents.getPopulationSize();
		for(int i = 0; i < popSize - 1; i+=2) {
			Individual p1 = parents.get(i);
			Individual p2 = parents.get(i+1);
			RealIndividual ch1 = (RealIndividual)p1.clone();
			RealIndividual ch2 = (RealIndividual)p2.clone();
			if (RandomNumberGenerator.getInstance().nextDouble() < xover_rate)
				cross(ch1, ch2);
			offspring.add(ch1);
			offspring.add(ch2);
		}
	}

	private void cross(RealIndividual a, RealIndividual b) {
		double y1, y2, y_low, y_hi, tmp;
		for (int i = 0; i < a.length(); i++) {
			y1 = (Double)a.get(i);
			y2 = (Double)b.get(i);
			if (Math.abs(y1 - y2) < EPS)
				continue;
			if (y1 > y2) {
				tmp = y1;
				y1 = y2;
				y2 = tmp;
			}
			y_low = a.getMin();
			y_hi = a.getMax();
			double rand = RandomNumberGenerator.getInstance().nextDouble();
			double beta = 1.0 + (2.0*(y1-y_low)/(y2-y1));
			double alpha = 2.0 - Math.pow(beta,-(ETA_C+1.0));
			double betaq = 0;
			if (rand <= (1.0/alpha))
            {
                betaq = Math.pow((rand*alpha),(1.0/(ETA_C+1.0)));
            }
            else
            {
                betaq = Math.pow((1.0/(2.0 - rand*alpha)),(1.0/(ETA_C+1.0)));
            }
			double c1 = 0.5*((y1+y2)-betaq*(y2-y1));
            beta = 1.0 + (2.0*(y_hi-y2)/(y2-y1));
            alpha = 2.0 - Math.pow(beta,-(ETA_C+1.0));
            if (rand <= (1.0/alpha))
            {
                betaq = Math.pow((rand*alpha),(1.0/(ETA_C+1.0)));
            }
            else
            {
                betaq = Math.pow((1.0/(2.0 - rand*alpha)),(1.0/(ETA_C+1.0)));
            }
            double c2 = 0.5*((y1+y2)+betaq*(y2-y1));
            if (c1<y_low)
                c1=y_low;
            if (c2<y_low)
                c2=y_low;
            if (c1>y_hi)
                c1=y_hi;
            if (c2>y_hi)
                c2=y_hi;
            if (RandomNumberGenerator.getInstance().nextDouble()<=0.5)
            {
                a.set(i, c2);
                b.set(i, c1);
            }
            else
            {
                a.set(i, c1);
                b.set(i, c2);
            }
		}
	}
}