package org.jquantlib.number;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;

import checkers.basetype.BaseTypeChecker;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.AnnotationFactory;
import checkers.util.SimpleSubtypeRelation;

/**
 * A simple checker that treats the {@code \@Expectation} annotation as a
 * subtype-style qualifier with no special semantics.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ExpectationChecker extends BaseTypeChecker {

    private SimpleSubtypeRelation relation;

    private AnnotationFactory annoFactory;

    /** Represents the {@code \@Expectation} annotation. */
    private AnnotationMirror annMirror;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        annoFactory = new AnnotationFactory(processingEnv);
        annMirror = this.annoFactory.fromName(Expectation.class.getCanonicalName());
        relation = new SimpleSubtypeRelation(annMirror, null);
    }

    @Override
    public boolean isSubtype(AnnotatedTypeMirror lhs, AnnotatedTypeMirror rhs) {
        return relation.isSubtype(lhs, rhs);
    }
}
