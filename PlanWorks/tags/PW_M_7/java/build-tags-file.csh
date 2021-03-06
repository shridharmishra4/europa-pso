#! /bin/tcsh -f
# $Id: build-tags-file.csh,v 1.28 2003-09-23 16:10:38 taylor Exp $
#
# build xemacs tags file on unix/linux based systems
#
set etags_flag = -f

if ( "`/bin/uname -s`" == "SunOS" ) then # SUN-OS OR SOLARIS
  if (-f /usr/ucb/hostid) then   # SOLARIS
    set etags_flag = -o
  endif
endif

etags $etags_flag planWorks.TAGS \
    src/gov/nasa/arc/planworks/PlanWorks.java \
    src/gov/nasa/arc/planworks/db/DbConstants.java \
    src/gov/nasa/arc/planworks/db/PwConstraint.java \
    src/gov/nasa/arc/planworks/db/PwDomain.java \
    src/gov/nasa/arc/planworks/db/PwEnumeratedDomain.java \
    src/gov/nasa/arc/planworks/db/PwIntervalDomain.java \
    src/gov/nasa/arc/planworks/db/PwModel.java \
    src/gov/nasa/arc/planworks/db/PwObject.java \
    src/gov/nasa/arc/planworks/db/PwParameter.java \
    src/gov/nasa/arc/planworks/db/PwPartialPlan.java \
    src/gov/nasa/arc/planworks/db/PwPlanningSequence.java \
    src/gov/nasa/arc/planworks/db/PwPredicate.java \
    src/gov/nasa/arc/planworks/db/PwProject.java \
    src/gov/nasa/arc/planworks/db/PwSlot.java \
    src/gov/nasa/arc/planworks/db/PwTimeline.java \
    src/gov/nasa/arc/planworks/db/PwToken.java \
    src/gov/nasa/arc/planworks/db/PwTokenRelation.java \
    src/gov/nasa/arc/planworks/db/PwTransaction.java \
    src/gov/nasa/arc/planworks/db/PwVariable.java \
    src/gov/nasa/arc/planworks/db/impl/PwConstraintImpl.java \
    src/gov/nasa/arc/planworks/db/impl/PwDomainImpl.java \
    src/gov/nasa/arc/planworks/db/impl/PwEnumeratedDomainImpl.java \
    src/gov/nasa/arc/planworks/db/impl/PwIntervalDomainImpl.java \
    src/gov/nasa/arc/planworks/db/impl/PwModelImpl.java \
    src/gov/nasa/arc/planworks/db/impl/PwObjectImpl.java \
    src/gov/nasa/arc/planworks/db/impl/PwParameterImpl.java \
    src/gov/nasa/arc/planworks/db/impl/PwPartialPlanImpl.java \
    src/gov/nasa/arc/planworks/db/impl/PwPlanningSequenceImpl.java \
    src/gov/nasa/arc/planworks/db/impl/PwPredicateImpl.java \
    src/gov/nasa/arc/planworks/db/impl/PwProjectImpl.java \
    src/gov/nasa/arc/planworks/db/impl/PwSlotImpl.java \
    src/gov/nasa/arc/planworks/db/impl/PwTimelineImpl.java \
    src/gov/nasa/arc/planworks/db/impl/PwTokenImpl.java \
    src/gov/nasa/arc/planworks/db/impl/PwTokenRelationImpl.java \
    src/gov/nasa/arc/planworks/db/impl/PwTransactionImpl.java \
    src/gov/nasa/arc/planworks/db/impl/PwVariableImpl.java \
    src/gov/nasa/arc/planworks/db/util/ContentSpec.java \
    src/gov/nasa/arc/planworks/db/util/FileUtils.java \
    src/gov/nasa/arc/planworks/db/util/MySQLDB.java \
    src/gov/nasa/arc/planworks/db/util/PwSQLFilenameFilter.java \
    src/gov/nasa/arc/planworks/mdi/EmptyDesktopIconUI.java \
    src/gov/nasa/arc/planworks/mdi/MDIDesktopFrame.java \
    src/gov/nasa/arc/planworks/mdi/MDIDesktopPane.java \
    src/gov/nasa/arc/planworks/mdi/MDIDynamicMenuBar.java \
    src/gov/nasa/arc/planworks/mdi/MDIFrame.java \
    src/gov/nasa/arc/planworks/mdi/MDIInternalFrame.java \
    src/gov/nasa/arc/planworks/mdi/MDIMenu.java \
    src/gov/nasa/arc/planworks/mdi/MDIWindowBar.java \
    src/gov/nasa/arc/planworks/mdi/MDIWindowButtonBar.java \
    src/gov/nasa/arc/planworks/mdi/SplashWindow.java \
    src/gov/nasa/arc/planworks/mdi/TileCascader.java \
    src/gov/nasa/arc/planworks/test/PlanWorksBigTest.java \
    src/gov/nasa/arc/planworks/test/PlanWorksTest.java \
    src/gov/nasa/arc/planworks/util/Algorithms.java \
    src/gov/nasa/arc/planworks/util/ColorMap.java \
    src/gov/nasa/arc/planworks/util/ColorStream.java \
    src/gov/nasa/arc/planworks/util/DirectoryChooser.java \
    src/gov/nasa/arc/planworks/util/DuplicateNameException.java \
    src/gov/nasa/arc/planworks/util/Extent.java \
    src/gov/nasa/arc/planworks/util/FileCopy.java \
    src/gov/nasa/arc/planworks/util/MouseEventOSX.java \
    src/gov/nasa/arc/planworks/util/OneToManyMap.java \
    src/gov/nasa/arc/planworks/util/ProjectNameDialog.java \
    src/gov/nasa/arc/planworks/util/ResourceNotFoundException.java \
    src/gov/nasa/arc/planworks/util/UniqueSet.java \
    src/gov/nasa/arc/planworks/util/Utilities.java \
    src/gov/nasa/arc/planworks/util/ViewRenderingException.java \
    src/gov/nasa/arc/planworks/viz/ViewConstants.java \
    src/gov/nasa/arc/planworks/viz/nodes/NodeGenerics.java \
    src/gov/nasa/arc/planworks/viz/nodes/TokenNode.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/ContentSpecChecker.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/RedrawNotifier.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/ViewManager.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/ViewSet.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/ViewSetRemover.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/contentSpecWindow/ContentSpecElement.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/contentSpecWindow/ContentSpecGroup.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/contentSpecWindow/ContentSpecWindow.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/contentSpecWindow/GroupBox.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/contentSpecWindow/KeyEntryBox.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/contentSpecWindow/LogicComboBox.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/contentSpecWindow/MergeBox.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/contentSpecWindow/NegationCheckBox.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/contentSpecWindow/PredicateBox.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/contentSpecWindow/PredicateGroupBox.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/contentSpecWindow/SpecBox.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/contentSpecWindow/TimeIntervalBox.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/contentSpecWindow/TimeIntervalGroupBox.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/contentSpecWindow/TimelineBox.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/contentSpecWindow/TimelineGroupBox.java \
    src/gov/nasa/arc/planworks/viz/viewMgr/contentSpecWindow/TokenTypeBox.java \
    src/gov/nasa/arc/planworks/viz/views/VizView.java \
    src/gov/nasa/arc/planworks/viz/views/constraintNetwork/BasicNodeLink.java \
    src/gov/nasa/arc/planworks/viz/views/constraintNetwork/BasicNodePortWDiamond.java \
    src/gov/nasa/arc/planworks/viz/views/constraintNetwork/ConstraintNetwork.java \
    src/gov/nasa/arc/planworks/viz/views/constraintNetwork/ConstraintNetworkLayout.java \
    src/gov/nasa/arc/planworks/viz/views/constraintNetwork/ConstraintNetworkView.java \
    src/gov/nasa/arc/planworks/viz/views/constraintNetwork/ConstraintNode.java \
    src/gov/nasa/arc/planworks/viz/views/constraintNetwork/VariableNode.java \
    src/gov/nasa/arc/planworks/viz/views/temporalExtent/TemporalExtentView.java \
    src/gov/nasa/arc/planworks/viz/views/temporalExtent/TemporalNode.java \
    src/gov/nasa/arc/planworks/viz/views/temporalExtent/TemporalNodeDurationBridge.java \
    src/gov/nasa/arc/planworks/viz/views/temporalExtent/TemporalNodeTimeMark.java \
    src/gov/nasa/arc/planworks/viz/views/timeline/SlotNode.java \
    src/gov/nasa/arc/planworks/viz/views/timeline/TimelineNode.java \
    src/gov/nasa/arc/planworks/viz/views/timeline/TimelineView.java \
    src/gov/nasa/arc/planworks/viz/views/tokenNetwork/TokenLink.java \
    src/gov/nasa/arc/planworks/viz/views/tokenNetwork/TokenNetworkLayout.java \
    src/gov/nasa/arc/planworks/viz/views/tokenNetwork/TokenNetworkView.java

echo "Writing `pwd`/planWorks.TAGS"
