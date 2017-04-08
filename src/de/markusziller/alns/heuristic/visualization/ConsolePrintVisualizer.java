package de.markusziller.alns.heuristic.visualization;

import de.markusziller.alns.entities.Insertion;
import de.markusziller.alns.entities.Solution;
import de.markusziller.alns.heuristic.strategies.alns.ALNSAbstractOperation;
import de.markusziller.alns.heuristic.strategies.alns.config.IALNSConfig;
import de.markusziller.alns.heuristic.strategies.alns.insertion.IALNSRepair;
import de.markusziller.alns.heuristic.strategies.alns.removal.IALNSDestroy;
import de.markusziller.alns.heuristic.strategies.phasetwo.ALNSProcess;

public class ConsolePrintVisualizer implements IProcessVisualizer, IStrategyVisualizer {

    @Override
    public void onThreadStart(ALNSProcess a) {

        IALNSConfig c = a.getConfig();
        System.out.println("----------------------------------------------------------------------------");
        System.out.printf("Thread %d spawned for s_c with current costs of %5.2f. Using config:" +
                        "\n omega \t tau \t r_p \t s1 \t s2 \t s3 \t c \t\t delta \t Omega " +
                        "\n %d \t %d \t %1.1f \t %d \t %d \t %d \t %-1.6f \t %-1.2f \t %-1.2f\n",
                Thread.currentThread().getId(),
                a.getS_g().getCosts(),
                c.getOmega(),
                c.getTau(),
                c.getR_p(),
                c.getSigma_1(),
                c.getSigma_2(),
                c.getSigma_3(),
                c.getC(),
                c.getDelta(),
                c.getBig_omega()
        );
        System.out.println("----------------------------------------------------------------------------");
    }

    @Override
    public void onStartConfigurationObtained(ALNSProcess a) {
        System.out.println("----------------------------------------------------------------------------");
        System.out.printf("Thread %d used this starting parameters: \n" +
                        "T_s \t T_end \t t_s\n" +
                        "%.1f \t %.1f \t %d\n",
                Thread.currentThread().getId(),
                a.getT_s(),
                a.getT_end(),
                a.getT_start()
        );
        System.out.println("----------------------------------------------------------------------------");
    }

    @Override
    public void onSolutionDestroy(ALNSProcess a, Solution s_destroy) {
        System.out.println("destroy");
    }

    @Override
    public void onSolutionRepaired(ALNSProcess a, Solution s_t) {
        System.out.println("repair");
    }

    @Override
    public void onDestroyRepairOperationsObtained(ALNSProcess a, IALNSDestroy _destroy, IALNSRepair _repair, Solution s_c_new, int q) {
        System.out.printf("[%d] %s, %s, T=%f, I_uns=%d \n", a.getI(), _destroy, _repair, a.getT(), s_c_new.getUnscheduledJobs().size());
    }

    @Override
    public void onAcceptancePhaseFinsihed(ALNSProcess a, Solution s_t) {
        System.out.printf("[t%d][i%d]\t f(s_g): %.1f| f(s_c): %.1f| f(s_t): %.1f| s_t/s_c: %.2f| T/T_s: %.2f| I_uns: %d  \n", a.getId(), a.getI(), a.getS_g().getCosts(), a.getS_c().getCosts(), s_t.getCosts(), s_t.getCosts() / a.getS_c().getCosts(), a.getT() / a.getT_s(), a.getS_c().getUnscheduledJobs().size());
    }

    @Override
    public void onJobPlanned(ALNSAbstractOperation a, Insertion i, Solution s) {
//		System.out.printf("%s: \tSuccesfully planned Job %d - %d remaining\n",a.getClass().getSimpleName(), i.getNode().getJob().getId(), s.getUnscheduledJobs().size());
    }

    @Override
    public void onIterationFinished(ALNSProcess a, Solution s_t) {
        if (a.getI() % a.getF_log() == 0 && a.getI() > 0) {
            long s = (System.currentTimeMillis() - a.getT_start()) / 1000;
            System.out.printf(a.getI() + " (%02d:%02d)\n", (s % 3600) / 60, (s % 60));

        }

    }

    @Override
    public void onSegmentFinsihed(ALNSProcess a, Solution s_t) {

    }

}
