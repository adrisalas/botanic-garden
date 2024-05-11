export function isImageUrl(image: string): boolean {
    return (image.includes("https") || image.includes(".com") || image.includes("www"))
}