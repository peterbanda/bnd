package com.bnd.function.business.ode

import org.junit.Test

import scala.collection.JavaConversions._
import com.bnd.function.business.ScalaFunctionEvaluatorConversions._
import java.{lang => jl, util => ju}

import com.bnd.function.domain.ODESolverType
import com.bnd.core.CollectionElementsConversions._
import org.junit.Assert._
import java.util.Random

import com.bnd.core.util.FileUtil
import com.bnd.core.dynamics.ODESolver
import com.bnd.core.util.FileUtil

class ODESolverTest {

    val adaptiveSolvers = Array(ODESolverType.RungeKuttaCashKarp,
        ODESolverType.RungeKuttaFehlberg, ODESolverType.RungeKuttaDormandPrince)
    val adaptiveNames = Array("RK Cash-Karp", "RK Fehlberg", "RK Dormand-Prince")
    val fileUtil = FileUtil.getInstance()

    @Test
    def timeAndAccuracy {
        val random = new Random()
        val tolerance = 0.1
        val elapsedTime = 200
        val runs = 10000

        for (s <- 0 to 0) {
            var times: Long = 0
            var errors = 0d

            for (i <- 1 to runs) {
                val FSolver = createAdaptiveChemSolver(adaptiveSolvers(s), tolerance, tolerance)
                val S1, S2, P = random.nextDouble() * 20
                val initialConcentrations: List[jl.Double] = List(S1, S2, P)
                val Pexpected = P + math.min(S1, S2)

                val min = if (s == 0) tolerance / 5 else tolerance
                val stream = iterateODESolver(FSolver, min)(initialConcentrations)
                val streamc = stream.map(_._2)
                val streamt = stream.map(_._1)

                // What I am timing is the determination of how many time steps covers the 'elapsed time,'
                // accessing that many concentrations coordinates and loading them into an array.
                val startTime = System.nanoTime()
                val listt = streamt takeWhile (_ < elapsedTime)
                val steps = listt.size

                val concentrations: Array[List[jl.Double]] = streamc take steps
                val time = System.nanoTime() - startTime

                // the error is the discrepancy between the expected final P concentration
                // and what the system produces
                val error = math.abs(Pexpected - concentrations(steps - 1)(2))
                times += time
                errors += error
            }
            val avgTime = (times / runs) / 1000000d
            // dividing by 1,000,000 converts avgTime to ms
            val avgError = errors / runs
            println(adaptiveNames(s) + " avgError = " + avgError + " avgTime = " + avgTime)
        }
    }

    def distance(x: List[jl.Double], y: List[jl.Double]): Double = {
        var dist = 0d
        for (i <- 0 to x.size - 1) {
            dist += math.pow((x(i) - y(i)), 2)
        }
        dist = math.sqrt(dist)
        dist
    }

    //    @Test
    //    def RK4comparison {
    //        val random = new Random()
    //        val stepSizes = Array(0.001, 0.005, 0.01, 0.05)
    //        val runs = 1000
    //        val elapsedTime = 5
    //        for (stepSize <- stepSizes) {
    //            println("Step Size: " + stepSize)
    //            var time: Long = 0 // The positions in these arrays correspond to
    //            var error = 0d // the adaptiveNames array
    //            val avgtimes = Array(0d, 0d, 0d)
    //            val avgErrors = Array(0d, 0d, 0d)
    //            val one: List[jl.Double] = List(1d, 1d)
    //            val two: List[jl.Double] = List(2d, 2d)
    //
    //            for (i <- 1 to runs) {
    //                // val refSolver = createAdaptiveLorenzSolver(ODESolverType.RungeKuttaCashKarp, 0.001, 0.001)
    //                val S1, S2, P = random.nextDouble() * 200 - 100
    //                val initialConcentrations: List[jl.Double] = List(S1, S2, P)
    //
    //                val refSolver = createAdaptiveLorenzSolver(ODESolverType.RungeKuttaCashKarp, 0.001, 0.001)
    //                val refstream = iterateODESolver(refSolver, 0.0002)(initialConcentrations)
    //                val refstreamv = refstream.map(_._2)
    //                val refstreamt = refstream.map(_._1)
    //
    //                val reflistt = refstreamt takeWhile (a => a < elapsedTime) // && ! a.isNaN() && ! a.isInfinite())
    //                val refsteps = reflistt.size
    //                val reffinalv = refstreamv.apply(refsteps)
    //
    //                val Solver = createChemSolver(ODESolverType.RungeKutta4, stepSize)
    //                val stream = iterateODESolver(Solver, stepSize)(initialConcentrations)
    //                val streamv = stream.map(_._2)
    //                val streamt = stream.map(_._1)
    //
    //                // What I am timing is the determination of how many time steps covers the 'elapsed time,'
    //                // accessing that many concentrations coordinates and loading them into an array.
    //                val startTime = System.nanoTime()
    //                val listt = streamt takeWhile (a => a < elapsedTime) // && ! a.isNaN() && ! a.isInfinite())
    //                val steps = listt.size
    //
    //                val concentrations: Array[List[jl.Double]] = streamv take steps
    //
    //                // the error is the discrepancy between the expected final P concentration
    //                // and what the system produces
    //                time = time + (System.nanoTime() - startTime)
    //                error = error + distance(streamv(steps),reffinalv)
    //            }
    //            val runTime = time / (runs * 1000000d)
    //            // dividing by 1,000,000 converts avgTime to ms
    //            error = error / runs
    //            println("avgError = " + error + " avgTime = " + runTime)
    //        }
    //    }

    //    @Test
    //    def timeAccLorenz {
    //        val random = new Random()
    //        val elapsedTime = 5
    //        val runs = 10
    //        val tolerances = Array(0.1) //0.001, 0.005, 0.01, 
    //
    //        for (tolerance <- tolerances) {
    //            println("Tolerance: " + tolerance)
    //            var times: Array[Long] = Array(0, 0, 0) // The positions in these arrays correspond to
    //            var errors = Array(0d, 0d, 0d) // the adaptiveNames array
    //            val avgtimes = Array(0d, 0d, 0d)
    //            val avgErrors = Array(0d, 0d, 0d)
    //            val one: List[jl.Double] = List(1d, 1d)
    //            val two: List[jl.Double] = List(2d, 2d)
    //
    //            for (i <- 1 to runs) {
    //                    val refSolver = createAdaptiveLorenzSolver(ODESolverType.RungeKuttaCashKarp, 0.001, 0.001)
    //                    val S1, S2, P = random.nextDouble() * 200 - 100
    //                    val initialConcentrations: List[jl.Double] = List(S1, S2, P)
    //    
    //                    val refstream = iterateODESolver(refSolver, 0.0002)(initialConcentrations)
    //                    val refstreamv = refstream.map(_._2)
    //                    val refstreamt = refstream.map(_._1)
    //    
    //                    val reflistt = refstreamt takeWhile (a => a < elapsedTime) // && ! a.isNaN() && ! a.isInfinite())
    //                    val steps = reflistt.size
    //                    val reffinalv = refstreamv.apply(steps)
    //                    println("Ref finished in " + steps + " steps.")
    //
    //                for (s <- 0 to 2) {
    //                    val Solver = createAdaptiveLorenzSolver(adaptiveSolvers(s), tolerance, tolerance)
    //
    //                    val min = if (s == 0) tolerance / 5 else tolerance
    //                    val stream = iterateODESolver(Solver, min)(initialConcentrations)
    //                    val streamv = stream.map(_._2)
    //                    val streamt = stream.map(_._1)
    //
    //                    // What I am timing is the determination of how many time steps covers the 'elapsed time,'
    //                    // accessing that many concentrations coordinates and loading them into an array.
    //                    val startTime = System.nanoTime()
    //                    val listt = streamt takeWhile (a => a < elapsedTime) // && ! a.isNaN() && ! a.isInfinite())
    //                    val steps = listt.size
    //
    //                    val concentrations: Array[List[jl.Double]] = streamv take steps
    //                    val time = System.nanoTime() - startTime
    //
    //                    // the error is the discrepancy between the expected final P concentration
    //                    // and what the system produces
    //                    val error = distance(concentrations(steps - 1), reffinalv)
    //                    times(s) += time
    //                    errors(s) += error
    //
    //                }
    //            }
    //            for (s <- 0 to 2) {
    //                avgtimes(s) = (times(s) / runs) / 1000000d
    //                // dividing by 1,000,000 converts avgTime to ms
    //                avgErrors(s) = errors(s) / runs
    //                println(adaptiveNames(s) + " avgError = " + avgErrors(s) + " avgTime = " + avgtimes(s))
    //            }
    //        }
    //    }

    // lookAtAdaptive prints two plots, one of the spacial coordinates and another
    // of the time step size, both over time
    //    @Test
    //    def lookAtAdaptive {
    //
    //        val random = new Random()
    //        val tolerance = 0.1
    //        val elapsedTime = 200
    //
    //        for (i <- 1 to 100) {
    //            val FSolver = createAdaptiveChemSolver(ODESolverType.RungeKuttaFehlberg, 0.001, tolerance)
    //            val S1, S2, P = random.nextDouble() * 20
    //            val xStart: List[jl.Double] = List(S1, S2, P)
    //            println(i + ": " + S1 + ", " + S2 + ", " + P)
    //
    //            // As an experiment, I'm making the min value a function of the tolerance
    //            val stream = iterateODESolver(FSolver, 0)(xStart)
    //            val streamx = stream.map(_._2)
    //            val streamt = stream.map(_._1)
    //
    //            val listt = streamt takeWhile (_ < elapsedTime)
    //            // You have to use 'take' on streamstep or it runs forever
    //            val steps = listt.size
    //
    //                val plotSetting = new TimeSeriesPlotSetting {
    //                    title = "RK Fehlberg"
    //                    transposed = true
    //                    captions = List("S1", "S2", "P")
    //                    xAxis = stream.map(_._1) take steps
    //                }
    //            val plotStepSetting = new TimeSeriesPlotSetting {
    //                title = "Runge Kutta Fehlberg"
    //                transposed = false
    //                captions = List("time step")
    //                xAxis = stream.map(_._1) take steps
    //            }
    //
    //            plotter.plotSeries(streamx take steps, plotSetting)
    //                fileUtil.overwriteStringToFileSafe(plotter.getOutput, i + "RK Fehlberg" + tolerance + ".svg")
    //        }
    //    }

    //    @Test
    //    def explosionTest {
    //        var count = 0
    //        val trials = 10000
    //        val tolerance = 0.01
    //        val elapsedTime = 20
    //
    //        val begin = System.nanoTime()
    //        for (i <- 1 to trials) {
    //
    //            val random = new Random()
    //            val FSolver = createAdaptiveChemSolver(ODESolverType.RungeKuttaFehlberg, 0.001, tolerance)
    //
    //            val S1, S2, P = random.nextDouble() * 20
    //            val xStart: List[jl.Double] = List(S1, S2, P)
    //
    //            // As han experiment, I'm making the min value a function of the tolerance
    //            val stream = iterateODESolver(FSolver, 0.00167)(xStart)
    //            val streamx = stream.map(_._2)
    //            val streamt = stream.map(_._1)
    //
    //            val listt = streamt takeWhile (_ < elapsedTime)
    //            // You have to use 'take' on streamstep or it runs forever
    //            val steps = listt.size
    //
    //            if ((streamx.flatten take steps * 3) exists (a => a > 100)) {
    //                count += 1
    //            }
    //        }
    //        val length = (System.nanoTime() - begin) * (math.pow(10, -6))
    //        println(count + " total explosions")
    //        println(length + " ms")
    //    }

    def avgTimeForTolerance(solver: ODESolverType, tolerance: Double): BigDecimal = {
        val LorenzSolver = createAdaptiveLorenzSolver(solver, 0.001, tolerance)
        val Start: List[jl.Double] = List(13, -8, 83)
        // The starting position and step size (0.001) are arbitrary
        // These might be worth reconsidering

        val stream = iterateODESolver(LorenzSolver, Double.MinValue)(Start)

        val avgTimeStep = (stream.map(_._1) apply 10000) / 10000
        return avgTimeStep
    }

    //    @Test
    //    def timeStepVsTolerance {
    //        val tolerances = Array(0.000001, 0.00001, 0.0001, 0.001, 0.01, 0.1, 1, 10)
    //        val avgtimes: Array[BigDecimal] = new Array[BigDecimal](8) 
    //        println("{tolerance, average time step}")
    //        // tests tolerances from 0.000001 to 10 by powers of 10
    //        for (i <- 0 to 7) {
    //            avgtimes(i) = avgTimeForTolerance(ODESolverType.RungeKuttaCashKarp, tolerances(i))
    //            println("{" + tolerances(i) + ", " + avgtimes(i) + "}")
    //        }
    //    }

    // The goal here is to see how long it takes the specified function to reach 
    // stoptime = 5
    //    @Test
    //    def toleranceTestCK {
    //        val tolerance = 0.0001
    //        val firststep = 0.001
    //        val CKSolver = createAdaptiveLorenzSolver(ODESolverType.RungeKuttaCashKarp, firststep, tolerance)
    //        val start: List[jl.Double] = List(13d, -8d, 83d)
    //        val stream = iterateODESolver(CKSolver, Double.MinValue)(start)
    //        val streamx = stream.map(_._2)
    //        val streamt = stream.map(_._1)
    //
    //        val stoptime = 5
    //        var time = 0d
    //        var steps = 0
    //        while (time < stoptime) {
    //            time = (streamt apply steps) // Checks the time after 'steps' steps
    //            steps += 1
    //        }
    //        println("{solver, initial stepsize, tolerance, stop time, number of steps}")
    //        println("{RKCK, " + firststep + ", " + tolerance + ", " + stoptime + ", " + steps + "}")
    //    }

    //     This checks the accuracy of RK2 against an example on Wikipedia.
    //     http://en.wikipedia.org/wiki/Runge%E2%80%93Kutta_methods#Usage
    //    @Test
    //    def accuracyTest {
    //        val rungeKutta2Solver = createTanODESolver(ODESolverType.RungeKutta2, 0.025)
    //        val yStart: List[jl.Double] = List(1)
    //
    //        val stream = iterateODESolver(rungeKutta2Solver, Double.MinValue)(yStart)
    //        val streamx = stream.map(_._2)
    //        val expecteds: List[jl.Double] = List(1.0, 1.066869388, 1.141332181, 1.227417567, 1.335079087)
    //        streamx take 5 foreach println
    //        ((streamx take 5), expecteds).zipped.foreach((a, b) => assertEquals(b, a.head, 0.000000001))
    //    }

    /*
    @Test
    def zzzzzzzz {
        val rungeKutta4Solver = createLorenzODESolver(ODESolverType.RungeKutta4, 0.001)
        val rungeKutta4TableauSolver = createLorenzODESolver(ODESolverType.RungeKutta4Tableau, 0.001)
        val xStart: List[jl.Double] = List(13, -8, 83)

        val stream1 = iterateODESolver(rungeKutta4Solver, Double.MinValue)(xStart)
        val stream1x = stream1.map(_._2)

        val stream2 = iterateODESolver(rungeKutta4TableauSolver, Double.MinValue)(xStart)
        val stream2x = stream2.map(_._2)

        val plotSetting = new TimeSeriesPlotSetting {
            title = "Concentrations"
            transposed = true
            captions = List("a", "b", "c")
            xAxis = stream1.map(_._1) take 3000
        }
        plotter.plotSeries(stream2x take 3000, plotSetting)
        fileUtil.overwriteStringToFileSafe(plotter.getOutput, "RK4.svg")

        // actual test : both streams must be equal
        ((stream1x take 2000).flatten, (stream2x take 2000).flatten)
        	   .zipped.foreach (assertEquals(_,_, 0.0000000000001))
    }
    var random = new Random()

    val runs = 10000
    val time = 1000
    val stepsize = 0.0005
    val tolerance = 0.9  // A good value for tolerance needs to be found; this one is arbitrary

    // Commented out is a series of tests which gets the diffs for each ODE
    // solver 'time' times, and averages THAT over 'runs' times.


    @Test
    def testRungeKutta4Time {
        var totalTime = 0D
        for (k <- 0 to runs) {
            val x = (random.nextDouble() * 200 - 100)
            val y = (random.nextDouble() * 200 - 100)
            val z = (random.nextDouble() * 200 - 100)
            // start is a list of 3 random numbers between -100 and 100
            val xStart: List[jl.Double] = List(x, y, z)
            val xxStart: Array[jl.Double] = xStart

            val begin = System.nanoTime()
            val rungeKutta4Solver = createLorenzODESolver(ODESolverType.RungeKutta4, stepsize)
            for (i <- 0 to time) {
                rungeKutta4Solver.getApproxDiffs(xxStart)
            }
            val length = System.nanoTime() - begin
            totalTime += length
        }
        val avgTime = totalTime / runs
        println(avgTime + " ns was the average RK4 runtime with {runs, timesteps, stepsize} = {" +
            runs + ", " + time + ", " + stepsize + "}")
    } 


    @Test
    def testRungeKutta4TabTime {
        var totalTime = 0D
        for (k <- 0 to runs) {
            val x = (random.nextDouble() * 200 - 100)
            val y = (random.nextDouble() * 200 - 100)
            val z = (random.nextDouble() * 200 - 100)
            val xStart: List[jl.Double] = List(x, y, z)
            val xxStart: Array[jl.Double] = xStart

            val begin = System.nanoTime()
            val rungeKutta4TableauSolver = createLorenzODESolver(ODESolverType.RungeKutta4Tableau, stepsize)
            for (i <- 0 to time) {
                rungeKutta4TableauSolver.getApproxDiffs(xxStart)
            }
            val length = System.nanoTime() - begin
            totalTime += length
        }
        val avgTime = totalTime / runs
        println(avgTime + " ns was the average RK4Tableau runtime with {runs, timesteps, stepsize} = {" +
            runs + ", " + time + ", " + stepsize + "}")

    }

    @Test
    def testRKCashKarp4Time {
        var totalTime = 0D
        for (k <- 0 to runs) {
            val random = new Random()
            val x = (random.nextDouble() * 200 - 100)
            val y = (random.nextDouble() * 200 - 100)
            val z = (random.nextDouble() * 200 - 100)

            val xStart: List[jl.Double] = List(x, y, z)
            val xxStart: Array[jl.Double] = xStart

            val begin = System.nanoTime()
            val rKCK4Solver = createLorenzODESolver(ODESolverType.RungeKuttaCashKarp4, stepsize)
            for (i <- 0 to time) {
                rKCK4Solver.getApproxDiffs(xxStart)
            }
            val length = System.nanoTime() - begin

            totalTime += length
        }
        val avgTime = totalTime / runs
        println(avgTime + " ns was the average RKCK4 runtime with {runs, timesteps, stepsize} = {" +
            runs + ", " + time + ", " + stepsize + "}")

    }



    @Test
    def testRKCashKarp5Time {
        var totalTime = 0D
        for (k <- 0 to runs) {
            val x = (random.nextDouble() * 200 - 100)
            val y = (random.nextDouble() * 200 - 100)
            val z = (random.nextDouble() * 200 - 100)

            val xStart: List[jl.Double] = List(x, y, z)
            val xxStart: Array[jl.Double] = xStart

            val begin = System.nanoTime()
            val rKCK5Solver = createLorenzODESolver(ODESolverType.RungeKuttaCashKarp5, stepsize)
            for (i <- 0 to time) {
                rKCK5Solver.getApproxDiffs(xxStart)
            }
            val length = System.nanoTime() - begin

            totalTime += length
        }
        val avgTime = totalTime / runs
        println(avgTime + " ns was the average RKCK5 runtime with {runs, timesteps, stepsize} = {" +
            runs + ", " + time + ", " + stepsize + "}")
    }

    @Test
    def testRKF4Time {
        var totalTime = 0D
        for (k <- 0 to runs) {
            val x = (random.nextDouble() * 200 - 100)
            val y = (random.nextDouble() * 200 - 100)
            val z = (random.nextDouble() * 200 - 100)

            val xStart: List[jl.Double] = List(x, y, z)
            val xxStart: Array[jl.Double] = xStart

            val begin = System.nanoTime()
            val rKF4Solver = createLorenzODESolver(ODESolverType.RungeKuttaFehlberg4, stepsize)
            for (i <- 0 to time) {
                rKF4Solver.getApproxDiffs(xxStart)
            }
            val length = System.nanoTime() - begin

            totalTime += length
        }
        val avgTime = totalTime / runs
        println(avgTime + " ns was the average RKF4 runtime with {runs, timesteps, stepsize} = {" +
            runs + ", " + time + ", " + stepsize + "}")
    }

    @Test
    def testRKF5Time {
        var totalTime = 0D

        for (k <- 0 to runs) {
            val x = (random.nextDouble() * 200 - 100)
            val y = (random.nextDouble() * 200 - 100)
            val z = (random.nextDouble() * 200 - 100)
            val xStart: List[jl.Double] = List(x, y, z)
            val xxStart: Array[jl.Double] = xStart

            val begin = System.nanoTime()
            val rKF5Solver = createLorenzODESolver(ODESolverType.RungeKuttaFehlberg5, stepsize)
            for (i <- 0 to time) {
                rKF5Solver.getApproxDiffs(xxStart)
            }
            val length = System.nanoTime() - begin
            totalTime += length
        }
        val avgTime = totalTime / runs
        println(avgTime + " ns was the average RKF5 runtime with {runs, timesteps, stepsize} = {" +
            runs + ", " + time + ", " + stepsize + "}")
    }

    @Test
    def testRKCKTime {
        var totalTime = 0D

        for (k <- 0 to runs) {
            val x = (random.nextDouble() * 200 - 100)
            val y = (random.nextDouble() * 200 - 100)
            val z = (random.nextDouble() * 200 - 100)
            val xStart: List[jl.Double] = List(x, y, z)
            val xxStart: Array[jl.Double] = xStart

            val begin = System.nanoTime()
            val rKF5Solver = createAdaptiveLorenzSolver(ODESolverType.RungeKuttaCashKarp, stepsize, tolerance)
            for (i <- 0 to time) {
                rKF5Solver.getApproxDiffs(xxStart)
            }
            val length = System.nanoTime() - begin
            totalTime += length
        }
        val avgTime = totalTime / runs
        println(avgTime + " ns was the average RKCK runtime with {runs, timesteps, stepsize} = {" +
            runs + ", " + time + ", " + stepsize + "}")
    }

    @Test
    def testRKDPTime {
        var totalTime = 0D

        for (k <- 0 to runs) {
            val x = (random.nextDouble() * 200 - 100)
            val y = (random.nextDouble() * 200 - 100)
            val z = (random.nextDouble() * 200 - 100)
            val xStart: List[jl.Double] = List(x, y, z)
            val xxStart: Array[jl.Double] = xStart

            val begin = System.nanoTime()
            val rKF5Solver = createAdaptiveLorenzSolver(ODESolverType.RungeKuttaDormandPrince, stepsize, tolerance)
            for (i <- 0 to time) {
                rKF5Solver.getApproxDiffs(xxStart)
            }
            val length = System.nanoTime() - begin
            totalTime += length
        }
        val avgTime = totalTime / runs
        println(avgTime + " ns was the average RKDP runtime with {runs, timesteps, stepsize} = {" +
            runs + ", " + time + ", " + stepsize + "}")
    }

    @Test
    def testRKFTime {
        var totalTime = 0D

        for (k <- 0 to runs) {
            val x = (random.nextDouble() * 200 - 100)
            val y = (random.nextDouble() * 200 - 100)
            val z = (random.nextDouble() * 200 - 100)
            val xStart: List[jl.Double] = List(x, y, z)
            val xxStart: Array[jl.Double] = xStart

            val begin = System.nanoTime()
            val rKF5Solver = createAdaptiveLorenzSolver(ODESolverType.RungeKuttaFehlberg, stepsize, tolerance)
            for (i <- 0 to time) {
                rKF5Solver.getApproxDiffs(xxStart)
            }
            val length = System.nanoTime() - begin
            totalTime += length
        }
        val avgTime = totalTime / runs
        println(avgTime + " ns was the average RKF runtime with {runs, timesteps, stepsize} = {" +
            runs + ", " + time + ", " + stepsize + "}")
    }

    @Test
    def testRKDP4Time {
        var totalTime = 0D

        for (k <- 0 to runs) {
            val x = (random.nextDouble() * 200 - 100)
            val y = (random.nextDouble() * 200 - 100)
            val z = (random.nextDouble() * 200 - 100)
            val xStart: List[jl.Double] = List(x, y, z)
            val xxStart: Array[jl.Double] = xStart

            val begin = System.nanoTime()
            val rKF5Solver = createLorenzODESolver(ODESolverType.RungeKuttaDormandPrince4, stepsize)
            for (i <- 0 to time) {
                rKF5Solver.getApproxDiffs(xxStart)
            }
            val length = System.nanoTime() - begin
            totalTime += length
        }
        val avgTime = totalTime / runs
        println(avgTime + " ns was the average RKDP4 runtime with {runs, timesteps, stepsize} = {" +
            runs + ", " + time + ", " + stepsize + "}")
    }

    @Test
    def testRKDP5Time {
        var totalTime = 0D

        for (k <- 0 to runs) {
            val x = (random.nextDouble() * 200 - 100)
            val y = (random.nextDouble() * 200 - 100)
            val z = (random.nextDouble() * 200 - 100)
            val xStart: List[jl.Double] = List(x, y, z)
            val xxStart: Array[jl.Double] = xStart

            val begin = System.nanoTime()
            val rKF5Solver = createLorenzODESolver(ODESolverType.RungeKuttaDormandPrince5, stepsize)
            for (i <- 0 to time) {
                rKF5Solver.getApproxDiffs(xxStart)
            }
            val length = System.nanoTime() - begin
            totalTime += length
        }
        val avgTime = totalTime / runs
        println(avgTime + " ns was the average RKDP5 runtime with {runs, timesteps, stepsize} = {" +
            runs + ", " + time + ", " + stepsize + "}")
    }
     */

    def iterateODESolver(solver: ODESolver, lowerBound: jl.Double)(x: List[jl.Double]) = {
        def iterate(time: Double, x: List[jl.Double]): Stream[(Double, List[jl.Double])] = {
            val newTime = time + solver.getTimeStep
            val xDiff: Iterable[jl.Double] = solver.getApproxDiffs(x: ju.List[jl.Double])
            val xNew: List[jl.Double] = (x, xDiff).zipped.map { (a, b) => a + b }
            val xNewNew: List[jl.Double] = xNew.map(a => if (a < lowerBound) 0: jl.Double else a)
            if (xNewNew.exists(_ < 0)) println("BAd bad")
            (newTime, xNewNew) #:: iterate(newTime, xNewNew)
        }
        (0d, x) #:: iterate(0d, x)
    }

    // createChemSolver models a simple AChem system with the reaction
    // S1 + S2 -> P with L as catalyst
    def createChemSolver(
        typ: ODESolverType,
        timeStep: jl.Double) = createODESolver(chemFun, 3, typ, timeStep)

    def createAdaptiveChemSolver(
        typ: ODESolverType,
        timeStep: jl.Double,
        tolerance: jl.Double) = createAdaptiveODESolver(chemFun, 3, typ, timeStep, tolerance)

    def createLorenzODESolver(
        typ: ODESolverType,
        timeStep: jl.Double) = createODESolver(lorenzFun(10, 8 / 3, 28)_, 3, typ, timeStep)

    def createAdaptiveLorenzSolver(
        typ: ODESolverType,
        timeStep: jl.Double,
        tolerance: jl.Double) = createAdaptiveODESolver(lorenzFun(10, 8 / 3, 28)_, 3, typ, timeStep, tolerance)

    def createTanODESolver(
        typ: ODESolverType,
        timeStep: jl.Double) = createODESolver(tanFun, 1, typ, timeStep)

    def createAdaptiveODESolver(
        fun: jl.Iterable[jl.Double] => Array[jl.Double],
        arity: Int,
        typ: ODESolverType,
        timeStep: jl.Double,
        tolerance: jl.Double) = ODESolverFactory.createInstance(
        scalaFunctionToFunctionEvaluator(fun, arity), typ, timeStep, tolerance)

    def createODESolver(
        fun: jl.Iterable[jl.Double] => Array[jl.Double],
        arity: Int,
        typ: ODESolverType,
        timeStep: jl.Double) = ODESolverFactory.createInstance(
        scalaFunctionToFunctionEvaluator(fun, arity), typ, timeStep)

    def lorenzFun(a: Double, b: Double, c: Double)(x: jl.Iterable[jl.Double]): Array[jl.Double] = {
        val xx = iterableAsScalaIterable(x).toList
        Array(
            a * (xx(1) - xx(0)),
            xx(0) * (c - xx(2)) - xx(1),
            xx(0) * xx(1) - b * xx(2))
    }

    def tanFun(y: jl.Iterable[jl.Double]): Array[jl.Double] = {
        val yy = iterableAsScalaIterable(y).toList
        Array[jl.Double](math.tan(yy(0)) + 1)
    }

    // chemFun is explained above with createChemSolver
    def chemFun(x: jl.Iterable[jl.Double]): Array[jl.Double] = {
        val xx = iterableAsScalaIterable(x).toList
        val l = 1.0
        Array(
            -l * xx(0) * xx(1),
            -l * xx(0) * xx(1),
            l * xx(0) * xx(1))
    }

}