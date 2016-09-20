package pro.documentum.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;

import pro.documentum.util.logger.LogLevel;
import pro.documentum.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public aspect LoggerAspect {

    pointcut error1(): call(static * Logger.error(String)) && !within(Logger) && !within(LoggerAspect);

    pointcut error2(): call(static * Logger.error(String, Throwable)) && !within(Logger) && !within(LoggerAspect);

    pointcut error3(): call(static * Logger.error(Throwable)) && !within(Logger) && !within(LoggerAspect);

    pointcut error4(): call(static * Logger.error(String, Object ...)) && !within(Logger) && !within(LoggerAspect);

    pointcut error5(): call(static * Logger.error(String, Throwable, Object ...)) && !within(Logger) && !within(LoggerAspect);

    pointcut warn1(): call(static * Logger.warn(String)) && !within(Logger) && !within(LoggerAspect);

    pointcut warn2(): call(static * Logger.warn(String, Throwable)) && !within(Logger) && !within(LoggerAspect);

    pointcut warn3(): call(static * Logger.warn(Throwable)) && !within(Logger) && !within(LoggerAspect);

    pointcut warn4(): call(static * Logger.warn(String, Object ...)) && !within(Logger) && !within(LoggerAspect);

    pointcut warn5(): call(static * Logger.warn(String, Throwable, Object ...)) && !within(Logger) && !within(LoggerAspect);

    pointcut debug1(): call(static * Logger.debug(String)) && !within(Logger) && !within(LoggerAspect);

    pointcut debug2(): call(static * Logger.debug(String, Throwable)) && !within(Logger) && !within(LoggerAspect);

    pointcut debug3(): call(static * Logger.debug(Throwable)) && !within(Logger) && !within(LoggerAspect);

    pointcut debug4(): call(static * Logger.debug(String, Object ...)) && !within(Logger) && !within(LoggerAspect);

    pointcut debug5(): call(static * Logger.debug(String, Throwable, Object ...)) && !within(Logger) && !within(LoggerAspect);

    pointcut info1(): call(static * Logger.info(String)) && !within(Logger) && !within(LoggerAspect);

    pointcut info2(): call(static * Logger.info(String, Throwable)) && !within(Logger) && !within(LoggerAspect);

    pointcut info3(): call(static * Logger.info(Throwable)) && !within(Logger) && !within(LoggerAspect);

    pointcut info4(): call(static * Logger.info(String, Object ...)) && !within(Logger) && !within(LoggerAspect);

    pointcut info5(): call(static * Logger.info(String, Throwable, Object ...)) && !within(Logger) && !within(LoggerAspect);

    pointcut trace1(): call(static * Logger.trace(String)) && !within(Logger) && !within(LoggerAspect);

    pointcut trace2(): call(static * Logger.trace(String, Throwable)) && !within(Logger) && !within(LoggerAspect);

    pointcut trace3(): call(static * Logger.trace(Throwable)) && !within(Logger) && !within(LoggerAspect);

    pointcut trace4(): call(static * Logger.trace(String, Object ...)) && !within(Logger) && !within(LoggerAspect);

    pointcut trace5(): call(static * Logger.trace(String, Throwable, Object ...)) && !within(Logger) && !within(LoggerAspect);

    void around(): error1() {
        log1(LogLevel.ERROR, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): error2() {
        log2(LogLevel.ERROR, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): error3() {
        log3(LogLevel.ERROR, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): error4() {
        log4(LogLevel.ERROR, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): error5() {
        log5(LogLevel.ERROR, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): warn1() {
        log1(LogLevel.WARN, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): warn2() {
        log2(LogLevel.WARN, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): warn3() {
        log3(LogLevel.WARN, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): warn4() {
        log4(LogLevel.WARN, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): warn5() {
        log5(LogLevel.WARN, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): debug1() {
        log1(LogLevel.DEBUG, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): debug2() {
        log2(LogLevel.DEBUG, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): debug3() {
        log3(LogLevel.DEBUG, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): debug4() {
        log4(LogLevel.DEBUG, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): debug5() {
        log5(LogLevel.DEBUG, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): info1() {
        log1(LogLevel.INFO, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): info2() {
        log2(LogLevel.INFO, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): info3() {
        log3(LogLevel.INFO, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): info4() {
        log4(LogLevel.INFO, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): info5() {
        log5(LogLevel.INFO, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): trace1() {
        log1(LogLevel.TRACE, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): trace2() {
        log2(LogLevel.TRACE, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): trace3() {
        log3(LogLevel.TRACE, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): trace4() {
        log4(LogLevel.TRACE, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    void around(): trace5() {
        log5(LogLevel.TRACE, thisJoinPoint, thisEnclosingJoinPointStaticPart);
    }

    protected void log1(LogLevel level, JoinPoint joinPoint, JoinPoint.StaticPart staticPart) {
        String source = getSource(staticPart);
        Object[] args = joinPoint.getArgs();
        Logger.log(level, source, (String) args[0]);
    }

    protected void log2(LogLevel level, JoinPoint joinPoint, JoinPoint.StaticPart staticPart) {
        String source = getSource(staticPart);
        Object[] args = joinPoint.getArgs();
        Logger.log(level, source, (String) args[0], (Throwable) args[1]);
    }

    protected void log3(LogLevel level, JoinPoint joinPoint, JoinPoint.StaticPart staticPart) {
        String source = getSource(staticPart);
        Object[] args = joinPoint.getArgs();
        Logger.log(level, source, null, (Throwable) args[0]);
    }

    protected void log4(LogLevel level, JoinPoint joinPoint, JoinPoint.StaticPart staticPart) {
        String source = getSource(staticPart);
        Object[] args = joinPoint.getArgs();
        Logger.log(level, source, (String) args[0], (Object[]) args[1]);
    }

    protected void log5(LogLevel level, JoinPoint joinPoint, JoinPoint.StaticPart staticPart) {
        String source = getSource(staticPart);
        Object[] args = joinPoint.getArgs();
        Logger.log(level, source, (String) args[0], (Throwable) args[1], (Object[]) args[2]);
    }

    protected String getSource(JoinPoint.StaticPart staticPart) {
        Signature callerSignature = staticPart.getSignature();
        return String.format("%s.%s", callerSignature.getDeclaringTypeName(), callerSignature.getName());
    }


}
