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
        if (!processes.isEmpty() && cpuEmpty) {
            // select the head of the queue
            Process p = processes.removeFirst();
            // increment context switches
            addContextSwitch();
            // load the process into CPU
            os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, p);
            // reset quantum counter for next time
            cont = 0;
            return;
        }

        // If CPU is not empty, enforce quantum expiration
        if (!cpuEmpty && !processes.isEmpty()) {
            // increment our internal quantum counter each scheduler tick
            cont++;
            if (cont >= q) {
                // time slice expired: preempt current process and rotate it to the back
                os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, null);
                // cont will be reset when the next process is scheduled (in above branch)
            }
        }
    }
    
    
    @Override
    public void newProcess(boolean cpuEmpty) {} //Non-preemtive in this event

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {} //Non-preemtive in this event
    
}
