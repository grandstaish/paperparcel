package nz.bradcampbell.paperparcel;

import android.os.Parcelable;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Can be applied to an BEAN-formatted object to automatically generate a "wrapper" class which
 * knows how to parcel and un-parcel that object.
 *
 * To wrap the annotated class with its generated wrapper, you can use {@link PaperParcels#wrap(Object)}.
 *
 * To unwrap a wrapped type, you can use {@link PaperParcels#unwrap(TypedParcelable)} or
 * {@link PaperParcels#unsafeUnwrap(Parcelable)}
 *
 * @see android.os.Parcel
 * @see android.os.Parcelable
 */
@Documented
@Retention(CLASS)
@Target(TYPE)
public @interface PaperParcel {
}
