let isVisible = true;

const toggleItemVisibility = (list) => {
  if(isVisible) {
    document.querySelector("." + list + ">.list-of-features").style.maxHeight = "0px";
    document.querySelector('.' + list + ">div>.arrow-before-title").style.transform = "rotate(90deg)";
    isVisible = false;
  } else {
    document.querySelector("." + list + ">.list-of-features").style.maxHeight = "700px";
    document.querySelector('.' + list + ">div>.arrow-before-title").style.transform = "rotate(0deg)";
    isVisible = true;
  }
}

export default toggleItemVisibility;