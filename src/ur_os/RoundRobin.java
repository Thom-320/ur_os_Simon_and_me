/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

/**
 *
 * @author prestamour
 */
public class RoundRobin extends Scheduler{

    int q;
    int cont;
    boolean multiqueue;
    
    RoundRobin(OS os){
        super(os);
        q = 4;
        cont=0;
    }
    
    RoundRobin(OS os, int q){
        this(os);
        this.q = q;
    }

    RoundRobin(OS os, int q, boolean multiqueue){
        this(os);
        this.q = q;
        this.multiqueue = multiqueue;
    }
    

    
    void resetCounter(){
        cont=0;
    }
   
    @Override
    public void getNext(boolean cpuEmpty) {
        //Insert code here
        // Case 1: CPU idle -> dispatch head (if any)
        if (cpuEmpty && !processes.isEmpty()) {
            Process p = processes.removeFirst();
            addContextSwitch();
            os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, p);
            cont = 0;
            return;
        }

        // Case 2: CPU busy -> check quantum
        if (!cpuEmpty) {
            cont++;
            if (cont >= q) {
                // Preempt current
                os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, null); // running process is appended to tail
                // Immediately dispatch next (eliminates idle '-1')
                if (!processes.isEmpty()) {
                    Process next = processes.removeFirst();
                    addContextSwitch();
                    os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, next);
                }
                cont = 0;
            }
        }
    }
    
    
    @Override
    public void newProcess(boolean cpuEmpty) {} //Non-preemtive in this event

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {} //Non-preemtive in this event
    
}
