import { getDownloadURL, ref, uploadBytes } from "firebase/storage";
import { getFirebaseStorage } from "./firebase";

export async function uploadFile(path: string, file: File): Promise<string> {
  const storageRef = ref(getFirebaseStorage(), path);
  await uploadBytes(storageRef, file);
  return getDownloadURL(storageRef);
}

export async function getFileUrl(path: string): Promise<string> {
  return getDownloadURL(ref(getFirebaseStorage(), path));
}
