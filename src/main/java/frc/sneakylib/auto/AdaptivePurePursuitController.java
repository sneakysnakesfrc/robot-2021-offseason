// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.sneakylib.auto;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import frc.robot.Constants;
import frc.robot.subsystems.DriveSubsytem;
import frc.sneakylib.math.Conversions;

/** Add your docs here. */
public class AdaptivePurePursuitController {
    private static DriveSubsytem m_drive;
    private static int m_lastClosestPointIndex;

    public AdaptivePurePursuitController(DriveSubsytem drive) {
        m_drive = drive;
    }

    public double[] update(Trajectory trajectory, Pose2d currentRobotPose, boolean reversed) {
        double heading = Math.toRadians(m_drive.getHeading());
        final double epsilon = 1E-10;
        if(heading == 0.0){
            heading = epsilon;
        }
        Translation2d lookahead = calculateLookahead(trajectory, currentRobotPose);
        double curvature = calculateCurvature(currentRobotPose, lookahead, heading);
        double targetVel = getPointVelocity(trajectory, m_lastClosestPointIndex);
        byte negate = 1;
        if(reversed == true){
            negate = -1;
        }
        double[] velocityArray = new double[2];
        double leftVel = negate * (targetVel * (2.0 + (curvature*Conversions.metersToInches(Constants.DriveConstants.kTrackwidthMeters)) / 2.0) );
        double rightVel = negate * (targetVel * (2.0 - (curvature*Conversions.metersToInches(Constants.DriveConstants.kTrackwidthMeters)) / 2.0 ) );
        velocityArray[0] = leftVel;
        velocityArray[1] = rightVel;
        return velocityArray;   
    }

    public static void reset(){
        m_lastClosestPointIndex = 0;
    }
    
    public static Translation2d calculateLookahead(Trajectory trajectory, Pose2d currentRobotPose) {
        m_lastClosestPointIndex =
                findClosestPointIndex(trajectory, currentRobotPose, m_lastClosestPointIndex);
        Translation2d lookahead = null;
        for (int i = m_lastClosestPointIndex; i < trajectory.getStates().size() - 2; i++) {
            Translation2d startPos = getPointPose(trajectory, i).getTranslation();
            Translation2d finishPos = getPointPose(trajectory, i + 1).getTranslation();
            Translation2d d = finishPos.minus(startPos);
            Translation2d f = startPos.minus(currentRobotPose.getTranslation());

            double a = calcDot(d, d);
            double b = 2 * calcDot(f, d);
            double c =
                    calcDot(f, f) - Math.pow(Constants.DriveConstants.kPurePursuitLookAheadDistance, 2.0);
            double dis = (b * b) - (4 * a * c);
            if (dis < 0) {
                continue;
            } else {
                dis = Math.sqrt(dis);
                double t1 = (-b - dis) / (2 * a);
                double t2 = (-b + dis) / (2 * a);
                if (t1 >= 0 && t1 <= 1) {
                    Translation2d temp = d.times(t1);
                    lookahead = startPos.plus(temp);
                    break;
                } else if (t2 >= 0 && t2 <= 1) {
                    Translation2d temp = d.times(t2);
                    lookahead = startPos.plus(temp);
                    break;
                }
            }
        }
        if (lookahead == null) {
            lookahead =
                    trajectory.getStates().get(trajectory.getStates().size() - 1).poseMeters.getTranslation();
        } else {
            double distToEnd =
                    currentRobotPose
                            .getTranslation()
                            .getDistance(
                                    trajectory
                                            .getStates()
                                            .get(trajectory.getStates().size() - 1)
                                            .poseMeters
                                            .getTranslation());
            if (distToEnd < Constants.DriveConstants.kPurePursuitLookAheadDistance) {
                lookahead =
                        trajectory
                                .getStates()
                                .get(trajectory.getStates().size() - 1)
                                .poseMeters
                                .getTranslation();
            }
        }
        return lookahead;
    }

    public static double calcDot(Translation2d firstVec, Translation2d secondVec) {
        return firstVec.getX() * secondVec.getX() + firstVec.getY() * secondVec.getY();
    }

    private static double calculateCurvature(Pose2d currentRobotPose, Translation2d lookahead, double heading) {
        double a = 1/Math.tan(heading);
        byte b = -1;
        double c = -a * currentRobotPose.getTranslation().getY() + currentRobotPose.getTranslation().getX();
        double x = Math.abs(a*lookahead.getY() + b* lookahead.getX() + c) / ((Math.sqrt(a * a + b * b)));
        double curvature = (2.0 * x) / (Math.pow(Constants.DriveConstants.kPurePursuitLookAheadDistance, 2.0));
        double side = Math.signum(
            Math.sin(heading) * (lookahead.getX() - currentRobotPose.getTranslation().getX()) -
            Math.cos(heading) * (lookahead.getY() - currentRobotPose.getTranslation().getY())
        );

        return curvature*side;
    }

    public static int findClosestPointIndex(Trajectory trajectory, Pose2d point, int lastIndex) {
        Translation2d lastPose = getPointPose(trajectory, lastIndex).getTranslation();
        double minDistance = point.getTranslation().getDistance(lastPose);
        int index = lastIndex;
        for (int i = lastIndex; i < trajectory.getStates().size() - 1; i++) {
            double tempDist =
                    point
                            .getTranslation()
                            .getDistance(trajectory.getStates().get(i).poseMeters.getTranslation());
            if (tempDist < minDistance) {
                index = i;
                minDistance = tempDist;
            }
        }
        return index;
    }

    public static double getPointCurvature(Trajectory trajectory, int index) {

        return trajectory.getStates().get(index).curvatureRadPerMeter;
    }

    public static Pose2d getPointPose(Trajectory trajectory, int index) {

        return trajectory.getStates().get(index).poseMeters;
    }

    public static double getPointVelocity(Trajectory trajectory, int index) {

        return trajectory.getStates().get(index).velocityMetersPerSecond;
    }

    
}
